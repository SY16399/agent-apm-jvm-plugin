package com.shenyang.javaagent.command;

import com.shenyang.javaagent.enhancer.AsmEnhancer;
import com.shenyang.javaagent.enhancer.MyAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/5 下午9:06
 */
public class ClassCommand {

    /**
     * 打印所有的类加载器
     *
     * @param inst 提供了一组用于操作字节码和监控 JVM 的方法。
     */
    public static void printAllClassLoader(Instrumentation inst) {
        //使用HashSet去重
        Set<ClassLoader> classLoaders = new HashSet<>();
        //获取所有的类
        System.out.println("所有的类加载器: ");
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class aClass : allLoadedClasses) {
            ClassLoader classLoader = aClass.getClassLoader();
            classLoaders.add(classLoader);
        }
        //打印类加载器
        String str = classLoaders.stream().map(x -> {
            if (x == null) {
                return "BootStrapClassLoader";
            }
            //反射类加载器DelegatingClassLoader的名字是空的,其实这个类加载器也没啥用
            if (x.getName() == null) {
                return "DelegatingClassLoader";
            }
            return x.getName();
        }).distinct().sorted(String::compareTo).collect(Collectors.joining(","));
        System.out.println(str);
    }

    /**
     * 打印类的源代码
     *
     * @param inst 提供了一组用于操作字节码和监控 JVM 的方法。
     */
    public static void printClassSourceCode(Instrumentation inst) {
        //添加一个反编译转换器
        changeClassByTransform(inst, new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                //通过jd-core反编译并打印源代码
                try {
                    printJDCoreSourceCode(classfileBuffer, className);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ClassFileTransformer.super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            }
        });
    }


    /**
     * 使用参考官方源码 ： https://github.com/java-decompiler/jd-core
     * 封装一个方法，通过jd-core打印源代码,截止2024年5月jd-core支持到JDK12 再往上可能不正确
     *
     * @param bytes     字节码信息
     * @param className 类名
     */
    public static void printJDCoreSourceCode(byte[] bytes, String className) throws Exception {
        //1.loader对象
        Loader loader = new Loader() {
            @Override
            public byte[] load(String internalName) throws LoaderException {
                return bytes;
            }

            @Override
            public boolean canLoad(String internalName) {
                //类是否能被加载，如果为true则打印不了源码了!!!!!
                return false;
            }
        };
        Printer printer = new Printer() {
            private static final String TAB = "  ";
            private static final String NEWLINE = "\n";

            private int indentationCount = 0;
            private final StringBuilder sb = new StringBuilder();

            @Override
            public String toString() {
                return sb.toString();
            }

            @Override
            public void start(int maxLineNumber, int majorVersion, int minorVersion) {
            }

            @Override
            public void end() {
                System.out.println(sb);
            }

            @Override
            public void printText(String text) {
                sb.append(text);
            }

            @Override
            public void printNumericConstant(String constant) {
                sb.append(constant);
            }

            @Override
            public void printStringConstant(String constant, String ownerInternalName) {
                sb.append(constant);
            }

            @Override
            public void printKeyword(String keyword) {
                sb.append(keyword);
            }

            @Override
            public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
                sb.append(name);
            }

            @Override
            public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
                sb.append(name);
            }

            @Override
            public void indent() {
                this.indentationCount++;
            }

            @Override
            public void unindent() {
                this.indentationCount--;
            }

            @Override
            public void startLine(int lineNumber) {
                sb.append(TAB.repeat(Math.max(0, indentationCount)));
            }

            @Override
            public void endLine() {
                sb.append(NEWLINE);
            }

            @Override
            public void extraLine(int count) {
                while (count-- > 0) sb.append(NEWLINE);
            }

            @Override
            public void startMarker(int type) {
            }

            @Override
            public void endMarker(int type) {
            }
        };
        //通过jd-core方法打印
        ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();

        decompiler.decompile(loader, printer, className);

    }


    /**
     * 对类进行增强，进入方法时输出方法开始，结束时输出方法结束。通过ASM
     *
     * @param inst 提供了一组用于操作字节码和监控 JVM 的方法。
     */
    public static void enhanceClassByAsm(Instrumentation inst) {
        //使用ASM字节码增强转换器
        changeClassByTransform(inst, new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                //通过ASM对类进行增强返回字节码信息
                return AsmEnhancer.enhanceClass(classfileBuffer);
            }
        });
    }

    /**
     * 对类进行增强，统计方法执行时间。通过ByteBuddy
     *
     * @param inst 提供了一组用于操作字节码和监控 JVM 的方法。
     */
    public static void enhanceClassByByteBuddy(Instrumentation inst) {
        //让用户输入类名
        System.out.println("请输入类名: ");
        Scanner scanner = new Scanner(System.in);
        String className = scanner.nextLine();
        //根据类名找到class对象
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class clazz : allLoadedClasses) {
            if (clazz.getName().equals(className)) {
                //找到了
                System.out.println("正在监控此方法的调用 !.....");
                //添加、移除转换器的工作都交给了ByteBuddy来进行
                //使用ByteBuddy来增强当前的类
                new AgentBuilder.Default()//模版代码
                        //禁止ByteBuddy在处理时修改类名
                        .disableClassFormatChanges()
                        //处理时使用retransform增强
                        .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                        //打印出错误的日志
                        .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting
                                .toSystemOut()))
                        //匹配哪些类 采用类名的方式
                        .type(ElementMatchers.named(className))
                        //增强使用MyAdvice 对所有的方法都进行增强
                        .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                                builder.visit(Advice.to(MyAdvice.class).on(ElementMatchers.any())))
                        .installOn(inst);
            }
        }
    }


    /**
     * 根据转换器对类进行相应的操作
     *
     * @param inst        提供了一组用于操作字节码和监控 JVM 的方法。
     * @param transformer 类的转换器
     */
    public static void changeClassByTransform(Instrumentation inst, ClassFileTransformer transformer) {
        //让用户输入类名
        System.out.println("请输入类名: ");
        Scanner scanner = new Scanner(System.in);
        String className = scanner.nextLine();
        //根据类名找到class对象
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class clazz : allLoadedClasses) {
            if (clazz.getName().equals(className)) {
                //找到了
                //1.添加一个转换器 使用对应的转换器   手动指定为可以转化才能看到字节码信息
                inst.addTransformer(transformer, true);
                //2.手动触发转换器
                try {
                    inst.retransformClasses(clazz);
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                } finally {
                    //3.删除转换器
                    inst.removeTransformer(transformer);
                }
                System.out.println(clazz);
                System.out.println("结束! 输入类名完毕!");
            }
        }
    }

    /*public static void enhanceClass(Instrumentation inst){
        //让用户输入类名
        System.out.println("请输入类名: ");
        Scanner scanner = new Scanner(System.in);
        String className = scanner.nextLine();
        //根据类名找到class对象
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class clazz : allLoadedClasses) {
            if (clazz.getName().equals(className)){
                //找到了
                //1.添加一个转换器
                ClassFileTransformer transformer = new ClassFileTransformer() {
                    @Override
                    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                        //通过ASM对类进行增强返回字节码信息
                        return AsmEnhancer.enhanceClass(classfileBuffer);
                    }
                };
                //手动指定为可以转化才能看到字节码信息
                inst.addTransformer(transformer,true);
                //2.手动触发转换器
                try {
                    inst.retransformClasses(clazz);
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                }finally {
                    //3.删除转换器
                    inst.removeTransformer(transformer);
                }
                System.out.println(clazz);
                System.out.println("结束! ");
            }
        }
    }*/

    /*public static void printClassSourceCode(Instrumentation inst){
        //让用户输入类名
        System.out.println("请输入类名: ");
        Scanner scanner = new Scanner(System.in);
        String className = scanner.nextLine();
        //根据类名找到class对象
        Class[] allLoadedClasses = inst.getAllLoadedClasses();
        for (Class clazz : allLoadedClasses) {
            if (clazz.getName().equals(className)){
                //找到了
                //1.添加一个转换器
                ClassFileTransformer transformer = new ClassFileTransformer() {
                    @Override
                    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                        //通过jd-core反编译并打印源代码
                        try {
                            printJDCoreSourceCode(classfileBuffer,className);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return ClassFileTransformer.super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                    }
                };
                //手动指定为可以转化才能看到字节码信息
                inst.addTransformer(transformer,true);
                //2.手动触发转换器
                try {
                    inst.retransformClasses(clazz);
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                }finally {
                    //3.删除转换器
                    inst.removeTransformer(transformer);
                }
                System.out.println(clazz);
                System.out.println("结束! ");
            }
        }
    }*/

}
