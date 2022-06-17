package com.sachin.agent.transformer;

import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.*;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

@SuppressWarnings("AlibabaRemoveCommentedCode")
public class HelloServiceTransformer implements ClassFileTransformer {

    private String agentArgs;

    public HelloServiceTransformer(String agentArgs) {
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

        className = className.replace("/", ".");
        System.out.println("HelloServiceTransformer#transform:" +
                className+!(className.contains("test.HelloService"))
        +" loader:"+loader
        );
        /**
         *         if (!className.equals("com/warrenyoung/instrumentions/instrumentiondest/InstrumentTestClass"))
         *         注意分割符号是/
         */
        if (!(className.contains("test.HelloService"))) {

            return classfileBuffer;
        }
        String agentParam = this.agentArgs;
        if (className.contains("HelloService")) {
            agentParam = "HelloService" + agentParam;
        }


        try {
            /**
             *
             * 类搜索路径
             * 通过 ClassPool.getDefault() 获取的 ClassPool 使用 JVM 的类搜索路径。如果程序运行在 JBoss 或者 Tomcat 等 Web 服务器上，
             * ClassPool 可能无法找到用户的类，因为 Web 服务器使用多个类加载器作为系统类加载器。在这种情况下，ClassPool 必须添加额外的类搜索路径。
             *
             * 下面的例子中，pool 代表一个 ClassPool 对象：
             * pool.insertClassPath(new ClassClassPath(this.getClass()));
             *
             * 将this指向的类添加到pool的类加载路径中。你可以使用任意class对象来代替this.getClass,从而将class对象添加到类加载路径中
             *
             * 也可以注册一个目录作为类搜索路径。下面的例子将 /usr/local/javalib 添加到类搜索路径中：
             * pool.insertClassPath("/usr/local/javalib");
             * 如果对 CtClass 对象调用 detach()，那么该 CtClass 对象将被从 ClassPool 中删除
             *
             *
             *
             */
//            ClassPool classPool = new ClassPool();
//            classPool.appendClassPath(new LoaderClassPath(loader));
//            final CtClass ctClass;
//            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(classfileBuffer)) {
//                ctClass = classPool.makeClass(byteArrayInputStream);
//            }
//            ctClass.getDeclaredMethod("getGreeting").setBody(" return \"  "+agentParam+"\" ;");;
//            System.out.println("transformed");
//            return ctClass.toBytecode();


            /**
             *  setBody的时候出现了异常
             * java.lang.RuntimeException: com.test.HelloService class is frozen
             *         at javassist.CtClassType.checkModify(CtClassType.java:321)
             *         at javassist.CtBehavior.setBody(CtBehavior.java:460)
             *         at javassist.CtBehavior.setBody(CtBehavior.java:440)
             *         at com.sachin.agent.transformer.HelloServiceTransformer.tran
             *
             *
             */

            /**
             *
             * 如果一个 CtClass 对象通过 writeFile(), toClass(), toBytecode() 被转换成一个类文件，此 CtClass
             * 对象会被冻结起来，不允许再修改。因为一个类只能被 JVM 加载一次。
             * 但是，一个冷冻的 CtClass 也可以被解冻，例如：
             * CtClasss cc = ...;
             *     :
             * cc.writeFile();
             * cc.defrost();
             * cc.setSuperclass(...);    // 因为类已经被解冻，所以这里可以调用成功
             * 调用 defrost() 之后，此 CtClass 对象又可以被修改了。
             *
             *注意：JVM 不允许动态重新加载类。一旦类加载器加载了一个类，它不能在运行时重新加载该类的修改版本。
             * 因此，在JVM 加载类之后，你不能更改类的定义。但是，JPDA（Java平台调试器架构）提供有限的重新加载类的能力。参见3.6节。
             *
             *
             * 如果相同的类文件由两个不同的类加载器加载，则 JVM 会创建两个具有相同名称和定义的不同的类。
             * 由于两个类不相同，一个类的实例不能被分配给另一个类的变量。两个类之间的转换操作将失败并抛出一个 ClassCastException。
             *
             *
             */
            CtClass ctClass = ClassPool.getDefault().get(className);
            //如果class文件被冻结，已经被jvm加载
            if(ctClass.isFrozen()) {
                ctClass.defrost();//解冻
            }
            CtMethod getGreeting = ctClass.getDeclaredMethod("getGreeting");
            getGreeting.setBody(" return \"  "+agentParam+"\" ;");
            return ctClass.toBytecode();


//            JavaClass javaClass = Repository.lookupClass(className);
//            ClassGen classGen = new ClassGen(javaClass);
//            ConstantPoolGen constantPool = classGen.getConstantPool();
//            for (Method method : javaClass.getMethods()) {
//
//                System.out.println("method:" + method.getName());
//
//                if (method.getName().equalsIgnoreCase("getGreeting")) {
//                    MethodGen methodGen = new MethodGen(method,classGen.getClassName(),constantPool);
//                    InstructionList instructionList = new InstructionList();
//                    instructionList.append(new PUSH(constantPool, agentParam));
//
//                    instructionList.append(InstructionFactory.createReturn(Type.STRING));
//                    methodGen.setInstructionList(instructionList);
//                    methodGen.setMaxStack();
//                    methodGen.setMaxLocals();
//                    classGen.replaceMethod(method, methodGen.getMethod());
//                    return classGen.getJavaClass().getBytes();
//                }
//            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


        return new byte[0];
    }
}
