package agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class PremainAgentTwo {


    public static void premain(String args, Instrumentation instrumentation) {


        new AgentBuilder.Default().type(ElementMatchers.named("com.demo.App"))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
                        return builder.method(ElementMatchers.named("getGreeting")).intercept(FixedValue.value(args));
                    }

                }).installOn(instrumentation);

    }


    public static void agentMain(String args, Instrumentation instrumentation) {
        new AgentBuilder.Default().with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                .disableClassFormatChanges()
                .type(ElementMatchers.named("com.demo.App"))
                .transform(new AgentBuilder.Transformer() {
                    @Override
                    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
                        return builder.method(ElementMatchers.named("getGreeting"))
                                .intercept(FixedValue.value((args)));
                    }
                }).installOn(instrumentation);
        /**
         * 另外，Byte Buddy 对 Attach API 作了封装，屏蔽了对 tools.jar 的加载，可以直接使用 ByteBuddyAgent 类：
         *
         * ByteBuddyAgent.attach(new File(agentJar), jvmPid, options);
         */
    }
}
