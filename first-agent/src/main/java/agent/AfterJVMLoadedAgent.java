package agent;

import com.sachin.agent.transformer.GreetingTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class AfterJVMLoadedAgent {


    static  String redefineClassName = "com.test.HelloService";

    /**
     *
     *
     *
     *
     * 启动后加载 agent 也是类似，
     * 通过 Agent-Class 属性指定 代理类，代理类 要实现 agentemain 静态方法。agent 被加载后，JVM 将尝试调用 agentmain 方法。
     *
     * 注意： 如果是以attach的方式使用的，则可能需要在agentmain中显式调用retransformClasses/redefineClasses，因为attach的时候用户类可能已经被加载过了
     *
     * @param agentArgs
     * @param instrumentation
     * @throws UnmodifiableClassException
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Exception {
        System.out.println("MyAgent#agentmain");
        instrumentation.addTransformer(new GreetingTransformer(agentArgs), true);

        Class<?> aClass = Class.forName(redefineClassName);
        instrumentation.retransformClasses(aClass);

    }
}
