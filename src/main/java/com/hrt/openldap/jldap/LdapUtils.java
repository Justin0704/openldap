package com.hrt.openldap.jldap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.LDAPSearchResults;
import com.novell.ldap.util.Base64;

public class LdapUtils {
    
    private static Logger log = null;
    static{
        log = LoggerFactory.getLogger(LdapUtils.class);
    }
    private String ldapHost = "localhost";
    private String loginDN = "cn=Manager,dc=hrt,dc=com";
    private String baseDN = "dc=hrt,dc=com";
    private String password = "secret";
    private String searchBase = "dc=hrt,dc=com";
    private String searchFilter = "objectClass=*";
    //查询范围
    private int searchScope = LDAPConnection.SCOPE_SUB;
    
    private int ldapPort = LDAPConnection.DEFAULT_PORT;
    private int ldapVersion = LDAPConnection.LDAP_V3;
    
    
    
    private LDAPConnection ldapConn = null;
    
    public static void main(String[] args) {
        //new LdapUtils().search();
        new LdapUtils().search("Developer");
        //new LdapUtils().search("michael", "Developer");
        //new LdapUtils().add();
        //new LdapUtils().delete();
        //new LdapUtils().modify();
        //new LdapUtils().verifyPwd();
    }
    
    /**
     * 获取LDAPConnection对象
     * @return
     * @throws LDAPException
     * @throws UnsupportedEncodingException
     */
    private LDAPConnection getLdapConnection(){
        LDAPConnection conn = new LDAPConnection();
        try{
            conn.connect(ldapHost, ldapPort);
            conn.bind(ldapVersion, loginDN, password.getBytes("UTF-8"));
            log.info("成功连接openLDAP服务器.");
        }catch(LDAPException e){
            if(e.getResultCode() == LDAPException.CONNECT_ERROR){
                log.error("LDAPException Error: 连接openLDAP服务器失败!!!");
            }else{
                log.error("LDAPException Error: " + e.getMessage());
            }
        }catch(UnsupportedEncodingException e){
            log.error("UnsupportedEncodingException Error：" + e.getMessage());
        }
        return conn;
    }
    /**
     * 通过根节点进行查询
     */
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
        }finally{
            closeLdapConnection(ldapConn);
        }
    }
    /**
     * 查询某个组织的信息
     * @param ou
     */
    @SuppressWarnings("unchecked")
    public void search(String ou){
        try{
            ldapConn = getLdapConnection();
            LDAPSearchResults searchResults = ldapConn.search("ou=" + ou + "," + searchBase, searchScope, searchFilter, null, false);
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
        }finally{
            closeLdapConnection(ldapConn);
        }
    }
    /**
     * 查询某个组织下的用户信息
     * @param uid
     * @param ou
     */
    @SuppressWarnings("unchecked")
    public void search(String uid,String ou){
        try{
            ldapConn = getLdapConnection();
            LDAPSearchResults searchResults = ldapConn.search("uid=" + uid + ",ou=" + ou + "," + searchBase, searchScope, searchFilter, null, false);
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
        }finally{
            closeLdapConnection(ldapConn);
        }
    }
    /**
     * 查询用户是否存在
     * @param uid
     * @param ou
     */
    public void checkDNExists(String userDN){
        try{
            ldapConn = getLdapConnection();
            LDAPSearchResults searchResults = ldapConn.search(userDN, searchScope, searchFilter, null, false);
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
            }
        }catch(LDAPException e){
            System.out.println("Search Error: " + e.toString());
        }finally{
            closeLdapConnection(ldapConn);
        }
    }
    /**
     * 添加组织名称
     * 
     * @param ou 组织的名称
     * @param paraMap 添加组织的参数
     */
    public void addOrginization(String ou,Map<String,String> paraMap){
        try{
            ldapConn = getLdapConnection();
            LDAPAttributeSet attributeSet = new LDAPAttributeSet();
            if(paraMap != null && !paraMap.isEmpty()){
                for(Map.Entry<String, String> entry : paraMap.entrySet()){
                    attributeSet.add(new LDAPAttribute(entry.getKey(),entry.getValue()));
                }
                String dn = "ou=" + ou + "," + baseDN;
                LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
                ldapConn.add(newEntry);
                System.out.println("------->Added object: " + dn + " successfully.");
            }
        }catch(Exception ex){
            closeLdapConnection(ldapConn);
        }
    }
    /**
     * 添加用户
     * 
     * @param uid 用户的账号（用户的唯一标志）
     * @param ou 用户属于某个组织的名称
     * @param paraMap 用户的基本信息
     */
    public void addUser(String uid,String ou,Map<String,String> paraMap){
        try{
            ldapConn = getLdapConnection();
            LDAPAttributeSet attributeSet = new LDAPAttributeSet();
            if(paraMap != null && !paraMap.isEmpty()){
                for(Map.Entry<String, String> entry : paraMap.entrySet()){
                    attributeSet.add(new LDAPAttribute(entry.getKey(),entry.getValue()));
                }
                String dn = "uid=" + uid + ",ou=" + ou + "," + baseDN;
                LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
                ldapConn.add(newEntry);
                System.out.println("------->Added object: " + dn + " successfully.");
            }
        }catch(Exception ex){
            closeLdapConnection(ldapConn);
        }
    }
//    @Deprecated
//    public void add(){
//        try{
//            ldapConn = getLdapConnection();
//            
//            LDAPAttributeSet attributeSet = new LDAPAttributeSet();
//            attributeSet.add(new LDAPAttribute("objectclass", "inetOrgPerson"));
//            attributeSet.add(new LDAPAttribute("cn", "Justin Li"));
//            attributeSet.add(new LDAPAttribute("sn", "Li"));
//            attributeSet.add(new LDAPAttribute("mail", "123456@qq.com"));
//            attributeSet.add(new LDAPAttribute("labeledURI", "http://www.baidu.com"));
//            attributeSet.add(new LDAPAttribute("userPassword", "1111111"));
//            attributeSet.add(new LDAPAttribute("uid", "addnew"));
//            
//            String dn = "uid=addnew,ou=Developer," + "dc=hrt,dc=com";
//            
//            LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
//            
//            ldapConn.add(newEntry);
//            System.out.println("------->Added object: " + dn + " successfully.");
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }finally{
//            if(ldapConn.isConnected()){
//                try {
//                    ldapConn.disconnect();
//                } catch (LDAPException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
    /**
     * 删除某个组织下的用户
     * @param uid 用户的UID
     * @param ou 属于用户的组织
     */
    public void deleteUser(String uid,String ou){
        try{
            ldapConn = getLdapConnection();
            String dn = "uid=" + uid + ",ou=" + ou + "," + baseDN;
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
        }finally{
            closeLdapConnection(ldapConn);
        }
    }
    /**
     * 删除用户
     * @param deleteDN 删除指定的DN
     * @return true：成功 false：失败
     */
    public boolean deleteUser(String deleteDN){
        boolean correct = false;
        try{
            ldapConn = getLdapConnection();
            ldapConn.delete(deleteDN);
            System.out.println("------->Delete object: " + deleteDN + " successfully.");
            correct = true;
        }catch(LDAPException e){
            if(e.getResultCode() == LDAPException.NO_SUCH_OBJECT){
                System.err.println("Error: No such object");
            }else if(e.getResultCode() == LDAPException.INSUFFICIENT_ACCESS_RIGHTS){
                System.err.println("Error: Insufficient rights");
            }else{
                System.err.println("Error: " + e.toString());
            }
            correct = false;
        }finally{
            closeLdapConnection(ldapConn);
        }
        return correct;
    }
    /**
     * 添加属性
     * @param modifyDN 先指定DN要添加属性：例如uid=Michael,ou=Developer,dc=hrt,dc=com
     * @param attrName 属性的name
     * @param attrString 属性的value
     * @return
     */
    public void addAttribute(String modifyDN,String attrName,String attrString){
        try{
            ldapConn = getLdapConnection();
            List<LDAPModification> modList = new ArrayList<>();
            //Add a new value to the description attribute
            LDAPAttribute attribute = new LDAPAttribute(attrName, attrString);
            modList.add(new LDAPModification(LDAPModification.ADD, attribute));
            
            LDAPModification[] mods = new LDAPModification[modList.size()];
            mods = (LDAPModification[])modList.toArray(mods);
            
            ldapConn.modify(modifyDN, mods);
            System.out.println("LDAPAttribute add successful.");
        }catch(LDAPException e){
            e.printStackTrace();
        }finally{
            closeLdapConnection(ldapConn);
        }
    }
    /**
     * 修改属性
     * @param modifyDN 先指定DN要修改的属性：例如uid=Michael,ou=Developer,dc=hrt,dc=com
     * @param attrName 属性的name
     * @param attrString 属性的value
     * @return
     */
    public boolean modifyAttribute(String modifyDN,String attrName,String attrString){
        boolean correct = false;
        try{
            ldapConn = getLdapConnection();
            List<LDAPModification> modList = new ArrayList<>();
            //Replace the labeledURI address with a new value
            LDAPAttribute attribute = new LDAPAttribute(attrName, attrString);
            modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
            
            LDAPModification[] mods = new LDAPModification[modList.size()];
            mods = (LDAPModification[])modList.toArray(mods);
            
            ldapConn.modify(modifyDN, mods);
            System.out.println("LDAPAttribute modify successful.");
            correct = true;
        }catch(LDAPException e){
            e.printStackTrace();
            correct = false;
        }finally{
            closeLdapConnection(ldapConn);
        }
        return correct;
    }
    /**
     * 删除属性
     * @param modifyDN 先指定DN要删除的属性：例如uid=Michael,ou=Developer,dc=hrt,dc=com
     * @param attrName 删除只需要指定的属性
     * @return
     */
    public boolean deleteAttribute(String modifyDN,String attrName){
        boolean correct = false;
        try{
            ldapConn = getLdapConnection();
            List<LDAPModification> modList = new ArrayList<>();
            //delete the email attribute
            LDAPAttribute attribute = new LDAPAttribute(attrName);
            modList.add(new LDAPModification(LDAPModification.DELETE, attribute));
            
            LDAPModification[] mods = new LDAPModification[modList.size()];
            mods = (LDAPModification[])modList.toArray(mods);
            
            ldapConn.modify(modifyDN, mods);
            System.out.println("LDAPAttribute delete successful.");
            correct = true;
        }catch(LDAPException e){
            e.printStackTrace();
            correct = false;
        }finally{
            closeLdapConnection(ldapConn);
        }
        return correct;
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
        }finally{
            closeLdapConnection(ldapConn);
        }
    }
    
    /**
     * 用户验证
     * @param uid 用户的编号
     * @param verifyPassword 用户的密码
     * @param ou 用户属于某个组织名称
     * @return
     */
    public boolean verifyPwd(String uid,String verifyPassword,String ou){
        boolean correct = false;
        try{
            String verifyDn = "uid=" + uid + ",ou=" + ou + "," + baseDN;
            ldapConn = getLdapConnection();
            LDAPAttribute attr = new LDAPAttribute("userPassword",verifyPassword);
            correct = ldapConn.compare(verifyDn,attr);
            System.out.println(correct ? "The password is correct.^_^" : "The password is incorrect.!!!");
        }catch(LDAPException e){
            e.printStackTrace();
        }finally{
            closeLdapConnection(ldapConn);
        }
        return correct;
    }
    /**
     * 用户验证
     * @param verifyDN 直接传递拼接好要验证的DN：例如 uid=zhangsan,ou=department,dc=hrt,dc=com
     * @param verifyPassword 要验证的密码
     * @return
     */
    public boolean verifyPwd(String verifyDN,String verifyPassword){
        boolean correct = false;
        try{
            String verifyDn = verifyDN;
            ldapConn = getLdapConnection();
            LDAPAttribute attr = new LDAPAttribute("userPassword",verifyPassword);
            correct = ldapConn.compare(verifyDn,attr);
            System.out.println(correct ? "The password is correct.^_^" : "The password is incorrect.!!!");
        }catch(LDAPException e){
            e.printStackTrace();
        }finally{
            closeLdapConnection(ldapConn);
        }
        return correct;
    }
    /**
     * rename
     */
    public void renameRDN(){
        try{
            //connect to the server
            ldapConn = getLdapConnection();
            //authenticate to the server
            ldapConn.bind(ldapVersion, loginDN, password.getBytes("UTF-8"));
            //modifies the entry's RDN
            //ldapConn.rename(dn, newRdn, newParentdn, deleteOldRdn);
        }catch(LDAPException e){
            
        }catch(UnsupportedEncodingException e){
            
        }
    }
    /**
     * 关闭ldapConn连接
     * @param ldapConn
     */
    public void closeLdapConnection(LDAPConnection ldapConn){
        if(ldapConn != null && ldapConn.isConnected()){
            try {
                ldapConn.disconnect();
            } catch (LDAPException e) {
                ldapConn = null;
                e.printStackTrace();
            }
        }
    }
   
}
