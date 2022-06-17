package com.test;

import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import javassist.ClassPool;

import java.util.concurrent.atomic.AtomicInteger;

public class App {
    static AtomicInteger atomicInteger = new AtomicInteger(1);
    /**
     * jingrzhang@C02G44J2MD6T JavaAgent %
     * java -javaagent:"/Users/jingrzhang/sourceCodes/JavaAgent/first-agent/target/my-agent.jar=abc"
     * -cp "app-demo/target/classes" com.test.App
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        /**
         * 确认 agent 这个jar中 所依赖的 外部 jar的api  已经被 当前应用程序的 加载过，避免agenet运行时报 class找不到的问题
         *
         * 因为agent是attach到目标target 进程， 目标taget进程需要 已经加载了agent使用到的class
         *
         */
        System.out.println(ClassPool.class.getClasses());
        System.out.println(JavaClass.class.getClasses());

        while (true) {
            System.out.println("HelloService sout   :"+HelloService.getGreeting());
            System.out.println("App sout:" + getGreeting());
            Thread.sleep(5000L);
        }
    }

    public static String getGreeting() {
        return "hello world" + atomicInteger.getAndIncrement();
    }
}
