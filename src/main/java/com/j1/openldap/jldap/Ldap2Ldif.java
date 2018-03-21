package com.j1.openldap.jldap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPMessage;
import com.novell.ldap.LDAPResponse;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchQueue;
import com.novell.ldap.LDAPSearchResult;
import com.novell.ldap.LDAPSearchResultReference;
import com.novell.ldap.util.LDAPWriter;
import com.novell.ldap.util.LDIFWriter;

public class Ldap2Ldif {

    private String ldapHost = "localhost";
    private int ldapPort = LDAPConnection.DEFAULT_PORT;
    private String password = "secret";
    private String searchBase = "dc=hrt,dc=com";
    private int ldapVersion = LDAPConnection.LDAP_V3;
    
    private int ldapScope = LDAPConnection.SCOPE_SUB;
    private String searchFilter = "objectClass=*";
    
    private String loginDN = "cn=Manager,dc=hrt,dc=com";
    private LDAPConnection ldapConn = null;
    private LDAPMessage ldapMsg = null;
    private FileOutputStream fos = null;
    private String fileName = "ldap.ldif";
    public static void main(String[] args) {
        
        new Ldap2Ldif().ldad2transfer();
    }
    
    public void ldad2transfer(){
        
        try{
            ldapConn = new LDAPConnection();
            ldapConn.connect(ldapHost, ldapPort);
            ldapConn.bind(ldapVersion, loginDN, password.getBytes("UTF-8"));
            fos = new FileOutputStream(fileName);
            LDAPWriter writer = new LDIFWriter(fos);
            //asynchronous search
            LDAPSearchQueue queue = ldapConn.search(searchBase, ldapScope, searchFilter, null, false, (LDAPSearchQueue)null, (LDAPSearchConstraints)null);
            while((ldapMsg = queue.getResponse()) != null){
                //the message is a search result reference
                if(ldapMsg instanceof LDAPSearchResultReference){
                    String urls[] = ((LDAPSearchResultReference) ldapMsg).getReferrals();
                    System.out.println("Search result references:");
                    for(int i = 0;i < urls.length; i++){
                        System.out.println(urls[i]);
                    }
                }else if(ldapMsg instanceof LDAPSearchResult){//the message is a search result
                    writer.writeMessage(ldapMsg);
                }else{//the message is a search response
                    LDAPResponse response = (LDAPResponse)ldapMsg;
                    int status = response.getResultCode();
                    if(status == LDAPException.SUCCESS){
                        System.out.println("Asynchronous search succeeded.");
                    }else if(status == LDAPException.REFERRAL){
                        String urls[]=((LDAPResponse)ldapMsg).getReferrals();
                        System.out.println("Referrals:");
                        for ( int i = 0; i < urls.length; i++ ){
                            System.out.println(urls[i]);
                        }
                    }else{
                        System.out.println("Asynchronous search failed.");
                        throw new LDAPException( response.getErrorMessage(), status,response.getMatchedDN());
                    }
                }
            }
            //close the output stream
            writer.finish();
            fos.close();
            System.out.println("An LDIF content file was generated.");
            //disconnect with the server
            ldapConn.disconnect();
        }catch(LDAPException ex){
            ex.printStackTrace();
        }catch(UnsupportedEncodingException ex){
            ex.printStackTrace();
        }catch(FileNotFoundException ex){
            ex.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
}
