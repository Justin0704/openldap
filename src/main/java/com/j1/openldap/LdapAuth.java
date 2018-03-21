package com.j1.openldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class LdapAuth {

    private final String URL = "ldap://localhost:389/";
    private final String BASEDN = "dc=hrt,dc=com";   
    private final String factory = "com.sun.jndi.ldap.LdapCtxFactory";
    private LdapContext ctx = null;  
    private final Control[] connCtls = null;
    
    private void ldapConnect(){
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
        env.put(Context.PROVIDER_URL, URL + BASEDN);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        
        String root = "cn=Manager,dc=hrt,dc=com";
        env.put(Context.SECURITY_PRINCIPAL, root);
        env.put(Context.SECURITY_CREDENTIALS, "secret");
        try {
            ctx = new InitialLdapContext(env, connCtls);
            System.out.println("连接成功。。。。。");
        } catch (NamingException e) {
            System.out.println("连接失败。。。。。");
            e.printStackTrace();
        }
    }
    private void closeContext(){
        if(ctx != null){
            try {
                ctx.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    }
    private String getUserDN(String uid){
        String userDN = "";
        ldapConnect();
        try{
            
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> en = ctx.search("","uid=" + uid, constraints);
            if(en == null || !en.hasMoreElements()){
                System.out.println("未找到该用户。。。。。");
            }
            while(en != null && en.hasMoreElements()){
                Object obj = en.nextElement();
                if(obj instanceof SearchResult){
                    SearchResult sr = (SearchResult)obj;
                    userDN += sr.getName();
                    userDN += "," + BASEDN;
                }else{
                    System.out.println(obj);
                }
            }
        }catch(Exception ex){
            System.out.println("查找用户异常。。。。。");
            ex.printStackTrace();
        }
        return userDN;
    }
    public boolean authentication(String UID,String password){
        boolean valid = false;
        String userDN = getUserDN(UID);
        System.out.println("userDN = " + userDN);
        try {
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.reconnect(connCtls);
            System.out.println("验证通过。。。。。。");
            valid = true;
        } catch (NamingException e) {
            System.out.println("验证失败。。。。。。");
            valid =false;
            e.printStackTrace();
        }
        closeContext();
        return valid;
    }
    /**
     * 添加一个用户
     * @param usr
     * @param pwd
     * @return
     */
    public boolean addUser(String usr,String pwd){
        try{
            ldapConnect();
            BasicAttributes basicAttr = new BasicAttributes();
            BasicAttribute objclassSet = new BasicAttribute("objectclass");
            objclassSet.add("inetOrgPerson");
            basicAttr.put(objclassSet);
            basicAttr.put("sn", "Li");
            basicAttr.put("cn", usr);
            basicAttr.put("uid", "Jian");
            basicAttr.put("userPassword", pwd);
            ctx.createSubcontext("uid=Jian", basicAttr);
            return true;
        }catch(NamingException ex){
            ex.printStackTrace();
        }
        closeContext();
        return false;
    }
    
    
    public boolean updateUser(String usr,String pwd){
        try{
            ldapConnect();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        closeContext();
        return false;
    }
    
    public static void main(String[] args) {
        LdapAuth auth = new LdapAuth();
        if(auth.authentication("Miumiu", "111111")){
            System.out.println("----->该用户认证成功");
        }
//        auth.addUser("JianLi", "123456789");
    }
}
