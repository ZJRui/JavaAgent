package com.sachin.agent;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main {


    static String attachAppName = "app-demo-1.0-SNAPSHOT.jar";
    static String agentJarPath = "/Users/jingrzhang/sourceCodes/JavaAgent/first-agent/target/first-agent.jar";
    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        System.out.println("classpath："+System.getProperty("CLASSPATh"));
//        executorService.submit(() -> {
//            run();
//        });
        run();

    }

    public static void run()   {

        String options = "options";
        try{
            List<VirtualMachineDescriptor> virtualMachineDescriptorList = VirtualMachine.list();
            for (VirtualMachineDescriptor virtualMachineDescriptor : virtualMachineDescriptorList) {
                System.out.println("displayname:"+virtualMachineDescriptor.displayName());
                if (virtualMachineDescriptor.displayName().equalsIgnoreCase(attachAppName)) {
                    VirtualMachine virtualMachine = VirtualMachine.attach(virtualMachineDescriptor.id());
                    virtualMachine.loadAgent(agentJarPath, options);
                    virtualMachine.detach();
                    System.out.println("attached: "+virtualMachineDescriptor.displayName() + " , " + virtualMachineDescriptor.id());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            Thread.sleep(100000*11000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}

