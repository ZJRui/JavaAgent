package agent;

import com.sachin.agent.transformer.GreetingTransformer;
import com.sachin.agent.transformer.HelloServiceTransformer;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

@SuppressWarnings("AlibabaRemoveCommentedCode")
public class AfterJVMLoadedAgent {


    static  String redefineClassName = "com.test.HelloService";

    /**
     * 启动后加载 agent 也是类似，
     * 通过 Agent-Class 属性指定 代理类，代理类 要实现 agentemain 静态方法。agent 被加载后，JVM 将尝试调用 agentmain 方法。
     * <p>
     * 注意： 如果是以attach的方式使用的，则可能需要在agentmain中显式调用retransformClasses/redefineClasses，因为attach的时候用户类可能已经被加载过了
     *
     * @param agentArgs
     * @param instrumentation
     * @throws UnmodifiableClassException
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) throws Exception {
        System.out.println("AfterJVMLoadedAgent#agentmain");

        instrumentation.addTransformer(new HelloServiceTransformer(agentArgs), true);
        Class[] allLoadedClasses = instrumentation.getAllLoadedClasses();
        for (Class allLoadedClass : allLoadedClasses) {

           // System.out.println("AfterJVMLoadedAgent#agentmain loadedClass"+allLoadedClass.getName());
            if (allLoadedClass.getName().equalsIgnoreCase(redefineClassName)) {
                System.out.println(allLoadedClass.getName() + " retransform");
                /**
                 * 启动时加载agent，-javaagent传入的jar包需要在MANIFEST.MF中包含Premain-class属性，此属性的值是代理类的名称，并且这个代理类要实现
                 *  premain静态方法。启动后加载agent也是类似，通过agent-class属性指定代理类，代理类要实现agentmain 静态方法。 agent被加载后，JVM尝试调用
                 *  agentmain方法。
                 *
                 *  每次定义新类（ClassLoader.defineClass)时，都将调用转换器的transform方法。对于已经定义加载的类需要使用重定义类（
                 *  调用Instrumention.defineClass）或重转换类（调用Instrumentioin.transformClass）
                 *
                 *  需要注意的是，和定义新类不同，重定义类和重转换类，可能会更改方法体、常量池和属性，但不得添加、移除、
                 *  重命名字段或方法；不得更改方法签名、继承关系 [ javadoc ]。这个限制将来可能会通过
                 *  “JEP 159: Enhanced Class Redefinition” 移除 [ ref ]。
                 *
                 */
                instrumentation.retransformClasses(allLoadedClass);

                /**
                 *
                 *第二种方式 使用redefine来替换classDefinition，提供新class文件内容的byte
                 *ClassDefinition classDefinition = new ClassDefinition(allLoadedClass, "新class文件的byte".getBytes());
                 *instrumentation.redefineClasses(classDefinition);
                 *
                 */
            } else {
                System.out.println(allLoadedClass.getName() + "  not retransform");
            }

        }


    }
}
