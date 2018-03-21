package com.j1.openldap.jldap;

import java.io.UnsupportedEncodingException;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPJSSESecureSocketFactory;

public class GetAuthenticated {

    
    private int ldapVersion = LDAPConnection.LDAP_V3;
    private int ldapPort = LDAPConnection.DEFAULT_PORT;
    private int ldapSslPort = LDAPConnection.DEFAULT_SSL_PORT;
    
    
    public static void SslBind(int version,String host,int sslPort,String dn,String password){
        LDAPJSSESecureSocketFactory ssf = new LDAPJSSESecureSocketFactory();
        LDAPConnection conn = new LDAPConnection(ssf);
        try{
            System.out.println("ssl bind");
            //connect to the server
            conn.connect(host, sslPort);
            //authenticate to the server with the connection method
            try{
                conn.bind(version, dn, password.getBytes("UTF-8"));
            }catch(UnsupportedEncodingException u){
                throw new LDAPException("UTF8 invalid encoding", LDAPException.LOCAL_ERROR, null, u);
            }
            System.out.println((conn.isBound()) ? "\n\tAuthenticated to the server(ssl)!\n" : "\n\tNot authenticated to the server.\n");
            conn.disconnect();
        }catch(LDAPException ex){
            System.out.println("Error: " + ex.toString());
        }
        return;
    }
}
