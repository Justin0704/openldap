package com.hrt.openldap.jldap;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class TestLdapUtils extends TestCase{

    private LdapUtils ldapUtils;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ldapUtils = new LdapUtils();
    }
    /**
     * 查询
     */
    public void testSearch(){
        ldapUtils.search();
    }
    /**
     * 查询用户是否存在
     */
    public void testCheckUserDNExists(){
        ldapUtils.checkDNExists("uid=mMichael,ou=Developer,dc=hrt,dc=com");
    }
    /**
     * 添加组织
     */
    public void testAdd(){
        Map<String,String> paraMap = new HashMap<>();
        paraMap.put("objectclass", "organizationalUnit");
        paraMap.put("ou", "department");
        paraMap.put("description", "这是个部门组织");
        ldapUtils.addOrginization("department", paraMap);
    }
    
    /**
     * 添加用户
     */
    public void testAddUser(){
        Map<String,String> paraMap = new HashMap<>();
        paraMap.put("objectclass", "inetOrgPerson");
        paraMap.put("uid", "lijian");
        paraMap.put("userPassword", "12345678");
        paraMap.put("sn", "li");
        paraMap.put("cn", "li jian");
        paraMap.put("mail", "lijian1@j1.com");
        paraMap.put("labeledURI", "http://www.soso.com");
        
        ldapUtils.addUser("lijian", "department", paraMap);
    }
    /**
     * 删除用户
     */
    public void testDeleteUser(){
        ldapUtils.deleteUser("lijian", "department");
    }
    
    /**
     * 用户验证
     */
    public void testVerifyPwd(){
        String uid = "michael";
        String verifyPassword = "111111";
        String ou = "developer";
        ldapUtils.verifyPwd(uid, verifyPassword,ou);
    }
    
    /**
     * 添加属性
     */
    public void testAddAttribute(){
        String modifyDN = "dc=app1,dc=hrt,dc=com";
        String attrName = "description";
        String attrString = "this is a desc";
        ldapUtils.addAttribute(modifyDN, attrName, attrString);
    }
    /**
     * 修改属性
     */
    public void testModifyAttribute(){
        String modifyDN = "dc=app1,dc=hrt,dc=com";
        String attrName = "description";
        String attrString = "qqqqqq@qq.com";
        ldapUtils.modifyAttribute(modifyDN, attrName, attrString);
    }
    /**
     * 删除属性
     */
    public void testDeleteAttribute(){
        String modifyDN = "dc=app1,dc=hrt,dc=com";
        String attrName = "description";
        ldapUtils.deleteAttribute(modifyDN, attrName);
    }
}
