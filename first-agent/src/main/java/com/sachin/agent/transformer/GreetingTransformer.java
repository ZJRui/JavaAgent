package com.sachin.agent.transformer;

import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.*;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class GreetingTransformer implements ClassFileTransformer {

    private String agentArgs;

    public GreetingTransformer(String agentArgs) {
        this.agentArgs = agentArgs;
    }

    /**
     * debug agement如何实现
     *
     *
     * @param loader                the defining loader of the class to be transformed,
     *                              may be <code>null</code> if the bootstrap loader
     * @param className             the name of the class in the internal form of fully
     *                              qualified class and interface names as defined in
     *                              <i>The Java Virtual Machine Specification</i>.
     *                              For example, <code>"java/util/List"</code>.
     * @param classBeingRedefined   if this is triggered by a redefine or retransform,
     *                              the class being redefined or retransformed;
     *                              if this is a class load, <code>null</code>
     * @param protectionDomain      the protection domain of the class being defined or redefined
     * @param classfileBuffer       the input byte buffer in class file format - must not be modified
     *
     * @return
     * @throws IllegalClassFormatException
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        /**
         *
         * 在Instrumentation.addTransformer，添加ClassFileTransformer，在以下三种情形下ClassFileTransformer.transform会被执行
         *
         * 新的class被加载
         * Instrumentation.redefineClasses显式调用
         * addTransformer第二个参数为true时，Instrumentation.retransformClasses显式调用
         *
         *
         * 如果是以attach的方式使用的，则可能需要在agentmain中显式调用retransformClasses/redefineClasses，因为attach的时候用户类可能已经被加载过了。。
         *
         * 所以java agent的两种工作模式下，
         *
         * 如果是以 -javaagent方式使用的，则不需要在premain中显式调用retransformClasses/redefineClasses，因为premain方法是在所有用户类被加载之前执行的
         *
         * 如果是以attach的方式使用的，则可能需要在agentmain中显式调用retransformClasses/redefineClasses，因为attach的时候用户类可能已经被加载过了
         *
         * 在第一种方式下，由于java agent premain方法是在所有用户类被加载之前执行的，transform前后的类的结构可以完全不同；
         * 第二种方式下，由于java agent agentmain方法执行的时候，部分类已经被加载过了，如果需要重新加载已加载的类，为了保证transform之后的类仍然可用，
         * 要求新的类格式与老的类格式兼容，因为transform只是更新了类里内容，相当于只更新了指针指向的内容，并没有更新指针，
         * 避免了遍历大量已有类对象对它们进行更新带来的开销。限制如下：
         * 父类是同一个
         * 实现的接口数也要相同，并且是相同的接口
         * 类访问符必须一致
         * 字段数和字段名要一致
         * 新增或删除的方法必须是private static/final的
         * 可以修改方法
         *
         *
         *
         */

       // return getBytes(loader, className, classBeingRedefined, classfileBuffer);
        String newClassName = className.replace("/", ".");

        System.out.println(newClassName+ " :"+newClassName.equalsIgnoreCase("com.test.App"));
        if (newClassName.equalsIgnoreCase("com.test.App")) {
            try {
                CtClass ctClass = ClassPool.getDefault().get(newClassName);
                System.out.println("创建ClassPool");
                System.out.println("classPool"+ctClass);
                CtMethod getGreetingMethod = ctClass.getDeclaredMethod("getGreeting");
                getGreetingMethod.setBody(" return  \"app "+agentArgs+"\""+";");
                System.out.println("生成代码");
                return ctClass.toBytecode();

            } catch (Throwable e) {
                //这里要用Throwable，不要用Exception
                //我们的agent 使用的 字节码工具包， 如果在 运行的JVM进程中 没有就会抛出java.lang.NoClassDefFoundError: javassist/ClassPool
                //那么也就是说，需要将 agent中的所有包都 打包到 运行的 target jar中

                e.printStackTrace();

            }
        }
        return classfileBuffer;
    }

    private byte[] getBytes(ClassLoader loader, String className, Class<?> classBeingRedefined, byte[] classfileBuffer) {
        System.out.println("GreetingTransformer#transform:" + className +(className.contains("com/test/App"))
        +" classLoader:"+ loader +"classBeingRedefined:"+ classBeingRedefined +"classfileBuffer:"+ classfileBuffer);

        /**
         *         if (!className.equals("com/warrenyoung/instrumentions/instrumentiondest/InstrumentTestClass"))
         *         注意分割符号是/
         */
        if (!(className.contains("com/test/App"))) {
            return classfileBuffer;
        }
        String agentParam = this.agentArgs;
        if (className.contains("App")) {
            agentParam = "App" + agentParam;
        }


        try {
            JavaClass javaClass = Repository.lookupClass(classBeingRedefined);//返回null，
            System.out.println("agentParam:"+javaClass.getMethods());
            System.out.println("javaClass"+javaClass+" "+javaClass.getMethods().length);
            ClassGen classGen = new ClassGen(javaClass);
            ConstantPoolGen constantPool = classGen.getConstantPool();
            for (Method method : javaClass.getMethods()) {

                System.out.println("methodName:"+method.getName()+ method.getName().equalsIgnoreCase("getGreeting"));
                if (method.getName().equalsIgnoreCase("getGreeting")) {
                    MethodGen methodGen = new MethodGen(method,classGen.getClassName(),constantPool);
                    InstructionList instructionList = new InstructionList();
                    instructionList.append(new PUSH(constantPool, agentParam));

                    System.out.println(" replace  method getGreeting "+agentParam);
                    instructionList.append(InstructionFactory.createReturn(Type.STRING));
                    methodGen.setInstructionList(instructionList);
                    methodGen.setMaxStack();
                    methodGen.setMaxLocals();
                    classGen.replaceMethod(method, methodGen.getMethod());
                    return classGen.getJavaClass().getBytes();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
