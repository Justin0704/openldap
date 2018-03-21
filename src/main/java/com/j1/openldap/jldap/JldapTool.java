package com.j1.openldap.jldap;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Iterator;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPResponse;
import com.novell.ldap.LDAPResponseQueue;

public class JldapTool {

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
        new JldapTool().graceLogin();
    }
    
    public void graceLogin(){
        
        int rc;
        String msg;
        ldapConn = new LDAPConnection();
        LDAPResponseQueue queue = null;
        byte[] pw = null;
        try{
            pw = password.getBytes("UTF-8");
            ldapConn.connect(ldapHost, ldapPort);
            queue = ldapConn.bind(ldapVersion, loginDN, pw, (LDAPResponseQueue)null);
            LDAPResponse rsp = (LDAPResponse)queue.getResponse();
            rc = rsp.getResultCode();
            msg = rsp.getErrorMessage();
            if(rc == LDAPException.SUCCESS){
                System.out.println("Bind is successful.");
            }else{
                System.out.println("Bind failed.");
                throw new LDAPException( msg, rc, (String)null );
            }
            if(msg != null && msg.length() != 0){
                String messageID = msg.substring(msg.indexOf((int)'-'));
                int lastIndex = messageID.length()-1;
                while(true){
                    int charAscii = (int)messageID.charAt(lastIndex);
                    if (charAscii >= 48 && charAscii <= 57)
                        break;
                    int len = lastIndex;
                    lastIndex = lastIndex - 1;
                    messageID = messageID.substring(0, len);
                }
                if ( messageID.compareTo( "-223" ) == 0 ) {
                    System.out.println(
                        "Password is expired for loginDN: " + loginDN);
                    System.out.println("Grace login used:");
                    getGraceLoginInfo(ldapConn, loginDN);
                }
            }
            ldapConn.disconnect();
        }catch(LDAPException e){
            System.out.println( "Error: " + e.toString() );
            System.exit(1);
        }catch(UnsupportedEncodingException ex){
            ex.printStackTrace();
        }
        System.exit(0);
    }
    
    public static void getGraceLoginInfo(LDAPConnection conn,String dn){
        String attributeName;
        String returnAttrs[] = {"loginGraceRemaining","loginGranceLimit"};
        Enumeration allValues;
        LDAPAttribute attribute;
        LDAPAttributeSet attributeSet;
        try{
            LDAPEntry graceLogin = conn.read(dn, returnAttrs);
            attributeSet = graceLogin.getAttributeSet();
            Iterator allAttributes = attributeSet.iterator();
            while(allAttributes.hasNext()){
                attribute = (LDAPAttribute)allAttributes.next();
                attributeName = attribute.getName();
                allValues = attribute.getStringValues();
                String attrValue = (String)allValues.nextElement();
                System.out.println("  " + attributeName + ": "+ attrValue);
            }
        }catch(LDAPException e){
            System.err.println( "getGraceLoginInfo() Failed.");
            System.err.println( "Error: " + e.toString() );
            System.exit(1);
        }
    }
}
