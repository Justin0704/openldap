package com.hrt.openldap.jldap;

public class TestSwitch {

    public static void main(String[] args) {
        
        System.out.println(fun(2));
    }
    
    public static int fun(int k){
        
        switch (k) {
        case 2:
            k++;
        case 3:
        case 4:
            k++;
        default:
            k++;
        }
        return k;
    }
}
