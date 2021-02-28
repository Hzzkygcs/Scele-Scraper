package com.Hzz;

import java.util.Arrays;


class A extends Object{

}

class B extends A{

}

class C extends B{

}

class D extends Object{

}


public class laboratorium_uji_coba {
    
    public static void test(Object... asd){

    }
    
    public static void test(Integer... asd){

    }
    
    
    public static void main(String[] args) {
        A a = new A();
        B b = new B();
        C c = new C();
        D d = new D();
        
        System.out.println(a instanceof A);
        System.out.println(b instanceof A);
        System.out.println(c instanceof A);
        System.out.println();
        
        System.out.println(a instanceof B);
        System.out.println(b instanceof B);
        System.out.println(c instanceof B);
        System.out.println();
        
        System.out.println(a instanceof C);
        System.out.println(b instanceof C);
        System.out.println(c instanceof C);
        System.out.println();
        
        System.out.println(a instanceof Object);
        System.out.println(b instanceof Object);
        System.out.println(c instanceof Object);
        System.out.println();
        
        
        
        
        
//        System.out.println(bsd);
//        System.out.println(asd);
//        System.out.println(bsd instanceof Object);
//        System.out.println(asd instanceof Object);
//        System.out.println(bsd instanceof Integer);
//        System.out.println(asd instanceof Integer);
    
    }
}
