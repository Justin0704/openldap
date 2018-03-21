package com.j1.openldap;

import java.util.Hashtable;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LdapUtils {

    private DirContext dc = null;
    private String account = "Manager";//操作LDAP账户。默认就是manager
    private String password = "secret";//账户manager的密码
    String root = "dc=hrt,dc=com";//LDAP的根节点DC
    private String factory = "com.sun.jndi.ldap.LdapCtxFactory";
    
    
    public static void main(String[] args) {
        new LdapUtils();
    }
    
    public LdapUtils(){
        connectLdap();
        add();//添加节点
        /**
         * 删除"uid=hazel,ou=Demo,dc=app2,dc=hrt,dc=com"节点
         * 删除节点，先删除从属对象
         * LDAP: error code 66 - subordinate objects must be deleted first
         */
        //delete("dc=app2,dc=hrt,dc=com");
        /**
         * 修改"ou=Demo,dc=app1,dc=hrt,dc=com"属性
         */
        //modify("ou=Demo,dc=app1,dc=hrt,dc=com",3);
        /**
         * 重命名节点"ou=new,o=neworganization,dc=example,dc=com"
         * LDAP: error code 66 - subtree rename not supported
         * 只能重命名没有子节点的entity
         */
        //rename("ou=Demo,dc=app1,dc=hrt,dc=com","ou=Demo2,dc=app1,dc=hrt,dc=com");
        /**
         * 遍历所有根节点
         */
        //search("ou=Demo,dc=app1,dc=hrt,dc=com", "", "(objectclass=*)");
        /**
         * 遍历指定节点的分节点
         */
        //search("dc=app1,dc=hrt,dc=com","","(objectclass=*)");
        /**
         * 关闭连接
         */
        close();
    }
    private void connectLdap() {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
        env.put(Context.PROVIDER_URL, "ldap://localhost:389/");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "cn="+account + "," + root);
        env.put(Context.SECURITY_CREDENTIALS, password);
        
        try {
            dc = new InitialDirContext(env);
            System.out.println("认证成功");
        } catch (NamingException e) {
            System.out.println("认证失败");
            e.printStackTrace();
        }
    }
    public void close(){
        if(dc != null){
            try {
                dc.close();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    }
    public void add(){
        try{
            String newUserName = "lijian";
            BasicAttribute objClassSet = new BasicAttribute("objectClass");
            objClassSet.add("top");
            objClassSet.add("organizationalUnit");
            
            BasicAttributes attrs = new BasicAttributes();
            attrs.put(objClassSet);
            attrs.put("ou", newUserName);
            
            dc.createSubcontext("ou=" + newUserName + "," + root, attrs);
        }catch(Exception ex){
            System.out.println("Exception in add(): " + ex);
            ex.printStackTrace();
        }
    }
    public void delete(String dn){
        try{
            
            dc.destroySubcontext(dn);
            System.out.println("delete(): success");
        }catch(Exception ex){
            System.out.println("Exception in delete(): " + ex);
            ex.printStackTrace();
        }
    }
    public boolean modify(String dn,int flag){
        try{
            ModificationItem[] mods = new ModificationItem[1];
            
            Attribute attr0 = null;
            
            if(flag == 1){//添加属性
                attr0 = new BasicAttribute("description", "添加属性");
                mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr0);
            }else if(flag == 2){//指定key名，然后修改value的名称
                attr0 = new BasicAttribute("description", "修改属性");
                mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr0);
            }else if(flag == 3){//删除属性要指定key-value
                attr0 = new BasicAttribute("description", "修改属性");
                mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attr0);
            }
            dc.modifyAttributes(dn, mods);
            System.err.println("modified success...");
            return true;
        }catch(NamingException ex){
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }
    public boolean rename(String oldName,String newName){
        try{
            dc.rename(oldName, newName);
            System.err.println("rename success...");
        }catch(NamingException ex){
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }
    public void search(String base,String scope,String filter){
        SearchControls sc = new SearchControls();
        if(scope.equals("base")){
            sc.setSearchScope(SearchControls.OBJECT_SCOPE);
        }else if(scope.equals("one")){
            sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        }else{
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
        }
        NamingEnumeration ne = null;
        try{
            ne = dc.search(base,filter,sc);
            while(ne.hasMoreElements()){
                SearchResult sr = (SearchResult)ne.next();
                String name = sr.getName();
                if(base != null && !base.equals("")){
                    System.out.println("entry: " + name + "," + base);
                }else{
                    System.out.println("entry: " + name);
                }
                Attributes attrs = sr.getAttributes();
                NamingEnumeration ane = attrs.getAll();
                while(ane.hasMoreElements()){
                    Attribute attr = (Attribute)ane.next();
                    String attrType = attr.getID();
                    NamingEnumeration values = attr.getAll();
                    Vector vals = new Vector();
                    while (values.hasMore()) {
                        Object oneVal = values.nextElement();
                        if (oneVal instanceof String) {
                            System.out.println(attrType + ": " + (String) oneVal);
                        } else {
                            System.out.println(attrType + ": " + new String((byte[]) oneVal));
                        }
                    }
                }
            }
        }catch(Exception ex){
            System.err.println("rename() Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
