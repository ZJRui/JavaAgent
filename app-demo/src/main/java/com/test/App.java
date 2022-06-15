package com.test;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import javassist.ClassPool;

public class App {
    /**
     * jingrzhang@C02G44J2MD6T JavaAgent %
     * java -javaagent:"/Users/jingrzhang/sourceCodes/JavaAgent/first-agent/target/my-agent.jar=abc"
     * -cp "app-demo/target/classes" com.test.App
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println(ClassPool.class.getClasses());
        System.out.println(JavaClass.class.getClasses());

        while (true) {
            System.out.println(getGreeting());
            Thread.sleep(1000L);
        }
    }

    public static String getGreeting() {
        return "hello world";
    }
}
