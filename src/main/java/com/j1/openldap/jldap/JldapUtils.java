package com.j1.openldap.jldap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPSearchResults;
import com.novell.ldap.util.Base64;

public class JldapUtils {

    private String ldapHost = "localhost";
    private String loginDN = "cn=Manager,dc=hrt,dc=com";
    private String password = "secret";
    private String searchBase = "dc=hrt,dc=com";
    private String searchFilter = "objectClass=*";
    //查询范围
    private int searchScope = LDAPConnection.SCOPE_SUB;
    
    private int ldapPort = LDAPConnection.DEFAULT_PORT;
    private int ldapVersion = LDAPConnection.LDAP_V3;
    
    
    
    private LDAPConnection ldapConn = null;
    
    public static void main(String[] args) {
        new JldapUtils().search();
        //new JldapUtils().add();
        //new JldapUtils().delete();
        //new JldapUtils().modify();
        //new JldapUtils().verifyPwd();
    }
    
    /**
     * 获取LDAPConnection对象
     * @return
     * @throws LDAPException
     * @throws UnsupportedEncodingException
     */
    private LDAPConnection getLdapConnection() throws LDAPException,UnsupportedEncodingException{
        LDAPConnection conn = new LDAPConnection();
        conn.connect(ldapHost, ldapPort);
        conn.bind(ldapVersion, loginDN, password.getBytes("UTF-8"));
        System.out.println("login ldap server successfully.");
        
        return conn;
    }
    
    @SuppressWarnings("unchecked")
    public void search(){
        try{
            ldapConn = getLdapConnection();
            LDAPSearchResults searchResults = ldapConn.search(searchBase, searchScope, searchFilter, null, false);
            while(searchResults.hasMore()){
                LDAPEntry ldapEntry = null;
                try{
                    ldapEntry = searchResults.next();
                }catch(LDAPException e){
                    System.out.println("Error: " + e.toString());
                    if(e.getResultCode() == LDAPException.LDAP_TIMEOUT || e.getResultCode() == LDAPException.CONNECT_ERROR){
                        break;
                    }else{
                        continue;
                    }
                }
                System.out.println("DN =: " + ldapEntry.getDN());
                System.out.println("|----Attributes list: ");
                LDAPAttributeSet attributeSet = ldapEntry.getAttributeSet();
                Iterator<LDAPAttribute> allAttributes =  attributeSet.iterator();
                while(allAttributes.hasNext()){
                    LDAPAttribute attribute = allAttributes.next();
                    String attributeName = attribute.getName();
                    Enumeration<String> values = attribute.getStringValues();
                    while(values.hasMoreElements()){
                        String value = values.nextElement();
                        if(!Base64.isLDIFSafe(value)){
                            value = Base64.encode(value);
                        }
                        System.out.println("|---- ----" + attributeName + " = " + value);
                    }
                }
            }
        }catch(LDAPException e){
            System.out.println("Search Error: " + e.toString());
        }catch(UnsupportedEncodingException e){
            System.out.println("Search Error: " + e.toString());
        }finally{
            if(ldapConn.isConnected()){
                try {
                    ldapConn.disconnect();
                } catch (LDAPException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void add(){
        try{
            ldapConn = getLdapConnection();
            
            LDAPAttributeSet attributeSet = new LDAPAttributeSet();
            attributeSet.add(new LDAPAttribute("objectclass", "inetOrgPerson"));
            attributeSet.add(new LDAPAttribute("cn", "Justin Li"));
            attributeSet.add(new LDAPAttribute("sn", "Li"));
            attributeSet.add(new LDAPAttribute("mail", "123456@qq.com"));
            attributeSet.add(new LDAPAttribute("labeledURI", "http://www.baidu.com"));
            attributeSet.add(new LDAPAttribute("userPassword", "1111111"));
            attributeSet.add(new LDAPAttribute("uid", "addnew"));
            
            String dn = "uid=addnew,ou=Developer," + "dc=hrt,dc=com";
            
            LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
            
            ldapConn.add(newEntry);
            System.out.println("------->Added object: " + dn + " successfully.");
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            if(ldapConn.isConnected()){
                try {
                    ldapConn.disconnect();
                } catch (LDAPException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void delete(){
        try{
            ldapConn = getLdapConnection();
            String dn = "uid=addnew,ou=Developer," + "dc=hrt,dc=com";
            ldapConn.delete(dn);
            System.out.println("------->Delete object: " + dn + " successfully.");
            
        }catch(LDAPException e){
            if(e.getResultCode() == LDAPException.NO_SUCH_OBJECT){
                System.err.println("Error: No such object");
            }else if(e.getResultCode() == LDAPException.INSUFFICIENT_ACCESS_RIGHTS){
                System.err.println("Error: Insufficient rights");
            }else{
                System.err.println("Error: " + e.toString());
            }
        }catch(UnsupportedEncodingException e){
            System.out.println("Error: " + e.toString());
        }finally{
            if(ldapConn.isConnected()){
                try {
                    ldapConn.disconnect();
                } catch (LDAPException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void modify(){
        
        try{
            ldapConn = getLdapConnection();
            List<LDAPModification> modList = new ArrayList<>();
            
            //Add a new value to the description attribute
            String desc = "This object was modified at " + new Date();
            LDAPAttribute attribute = new LDAPAttribute("description", desc);
            modList.add(new LDAPModification(LDAPModification.ADD, attribute));
            
            attribute = new LDAPAttribute("telephoneNumber", "180-8888-xxxx");
            modList.add(new LDAPModification(LDAPModification.ADD, attribute));
            
            //Replace the labeledURI address with a new value
            attribute = new LDAPAttribute("labeledURI", "www.google.com");
            modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
            
            //delete the email attribute
            attribute = new LDAPAttribute("mail");
            modList.add(new LDAPModification(LDAPModification.DELETE, attribute));
            
            LDAPModification[] mods = new LDAPModification[modList.size()];
            mods = (LDAPModification[])modList.toArray(mods);
            
            String modifyDN = "uid=Michael,ou=Developer,dc=hrt,dc=com";
            
            ldapConn.modify(modifyDN, mods);
            System.out.println("LDAPAttribute add、replace、delete all successful.");
        }catch(LDAPException e){
            e.printStackTrace();
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }finally{
            if(ldapConn.isConnected()){
                try {
                    ldapConn.disconnect();
                } catch (LDAPException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //验证密码
    public void verifyPwd(){
        try{
            String verifyDn = "uid=Michael,ou=Developer,dc=hrt,dc=com";
            String verifyPassword = "111111";
            
            ldapConn = getLdapConnection();
            
            LDAPAttribute attr = new LDAPAttribute("userPassword",verifyPassword);
            boolean correct = ldapConn.compare(verifyDn,attr);
            System.out.println(correct ? "The password is correct.^_^" : "The password is incorrect.!!!");
        }catch(LDAPException e){
            e.printStackTrace();
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }finally{
            if(ldapConn.isConnected()){
                try {
                    ldapConn.disconnect();
                } catch (LDAPException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
