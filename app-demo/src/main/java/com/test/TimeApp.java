package com.test;

public class TimeApp {

    public static void main(String[] args) {
        while(true){
            System.out.println();
        }
    }
    public static String getGreeting(){

        try {
            Thread.sleep((long) (1000 * Math.random()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "hello world";
    }

}
