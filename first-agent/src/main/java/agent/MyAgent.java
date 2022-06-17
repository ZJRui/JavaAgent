package agent;

import com.sachin.agent.transformer.GreetingTransformer;
import com.sun.tools.attach.VirtualMachine;

import java.lang.instrument.Instrumentation;

@SuppressWarnings("AlibabaRemoveCommentedCode")
public class MyAgent {

    /**
     * Java Agent支持目标JVM启动时加载，也支持在目标JVM运行时加载，这两种不同的加载模式会使用不同的入口函数，如果需要在目标JVM启动的同时加载Agent，
     * 那么可以选择实现下面的方法：
     * [1] public static void premain(String agentArgs, Instrumentation inst);
     * [2] public static void premain(String agentArgs);
     *
     *
     *
     *
     * 启动时加载 agent，-javaagent 传入的 jar 包需要在 MANIFEST.MF 中包含 Premain-Class 属性，
     * 此属性的值是 代理类 的名称，并且这个 代理类 要实现 premain 静态方法。
     *
     *
     * Agent 是在 Java 虚拟机启动之时加载的，这个加载处于虚拟机初始化的早期，在这个时间点上：
     * 所有的 Java 类都未被初始化；
     * 所有的对象实例都未被创建；
     * 因而，没有任何 Java 代码被执行；
     *
     *
     *  java -javaagent:/Users/jingrzhang/sourceCodes/JavaAgent/first-agent/target/my-agent.jar=abc -jar  app-demo-1.0-SNAPSHOT.jar
     *
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("MyAgent#premain");
        System.out.println("args:" + agentArgs + "\n");
       inst.addTransformer(new GreetingTransformer(agentArgs),true);
    }

    /**
     * JDK1.6新增了attach方式，可以对运行中的java进程附加agent，提供了动态修改运行中已经被加载的类的途径。
     * 一般通过VirtualMachine的attach(pid)方法获得VirtualMachine实例，随后可调用loadagent方法将JavaAgent的jar包加载到目标JVM中。
     *
     * 在进程B中向进程A中注入java agent，需要满足以下条件：
     * 进程B的classpath中必须有tools.jar（提供VirtualMachine attach api），jdk默认有tools.jar，jre默认没有。
     *
     * tools.jar 这个包位于jdk的lib目录下， IDEA中安装的java 依赖是 JRE，也就是jdk安装目录下的jre/lib 目录
     *
     * 而这个jre/lib 目录下 没有 jdk目录下的tools.jar
     *
     *而这里用到的VirtualMachine是就是 tools.jar 包中的类。
     * import com.sun.tools.attach.VirtualMachine;
     * import com.sun.tools.attach.VirtualMachineDescriptor;
     *
     * 在jdk1.8中 tools.jar 包中没有了 VirtualMachine， 而是存在
     * sun.tools.attach.HotSpotVirtualMachine
     *
     * com.sun.tools.attach.VirtualMachine
     *
     */
    public static void loadVirtualMachine() throws Exception{

        String jvmPid="targetPid";
//        VirtualMachine jvm = VirtualMachine.attach(jvmPid);
//        jvm.loadAgent(agentFilePath);//agentFilePath为agent的路径
//        jvm.detach();
//        logger.info("Attached to target JVM and loaded Java agent successfully");

        //实际返回的是 hotspotVirtualMachine 实现类
        VirtualMachine virtualMachine = VirtualMachine.attach(jvmPid);
        virtualMachine.loadAgent("agentFilePath");
        virtualMachine.detach();
    }


}
