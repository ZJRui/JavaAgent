package agent.timer;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class TimerAgent {


    public static void premain(String args, Instrumentation instrumentation) {
        new AgentBuilder.Default().type(ElementMatchers.any())
                .transform((builder, typeDescription, classLoader) -> {
                    return builder.method(ElementMatchers.nameMatches(args))
                            /**
                             *
                             * Delegation API 实现的原理就是 将被拦截的方法委托到另一个办法上，如下左图所示（图片来自 Rafael Winterhalter 的 slides）
                             *
                             * 若要通过 Byte Buddy 实现启动后动态加载 agent，官方提供了 Advice API [ javadoc ]。
                             * Advice API 实现原理上是，在被拦截方法内部的开始和结尾添加代码，如下右图所示。这样只更改了方法体，
                             * 不更改方法签名，也没添加额外的方法，符合重定义类（redefineClass）和重转换类（retransformClass）的限制。
                             *
                             */
                            .intercept(MethodDelegation.to(TimingInterceptor.class));
                }).installOn(instrumentation);
    }

    public static void agentmain(String args, Instrumentation instrumentation) {
        new AgentBuilder.Default().disableClassFormatChanges().with(AgentBuilder.RedefinitionStrategy.REDEFINITION)
                .type(ElementMatchers.any())

                .transform(((builder, typeDescription, classLoader) ->
                        builder.visit(Advice.to(TimingAdvice.class)
                                .on(ElementMatchers.nameMatches(args)))
                )).installOn(instrumentation);

    }
}
