package com.j1.openldap.jldap;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.novell.ldap.LDAPMessage;
import com.novell.ldap.util.DSMLWriter;
import com.novell.ldap.util.LDIFReader;

public class Ldif2Dsml {

    
    public static void main(String[] args) {
        String fileName = "ldap.ldif";
        String ofileName = "filedsml.dsml";
        LDIFReader reader = null;
        LDAPMessage msg = null;
        DSMLWriter out = null;
        try{
            
            FileInputStream fis = new FileInputStream(fileName);
            reader = new LDIFReader(fis, 1);
            
            FileOutputStream fos = new FileOutputStream(ofileName);
            out = new DSMLWriter(fos);
            
            out.useIndent(true);
            out.setIndent(4);
            while ( (msg = reader.readMessage()) != null ) {
                out.writeMessage( msg);
            }
            out.finish();
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
