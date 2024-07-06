package com.shenyang.javaagent;

import com.shenyang.javaagent.command.ClassCommand;
import com.shenyang.javaagent.command.MemoryCommand;
import com.shenyang.javaagent.command.ThreadCommand;
import com.shenyang.javaagent.enhancer.AgentParam;
import com.shenyang.javaagent.enhancer.MyAdvice;
import com.shenyang.javaagent.enhancer.TimingAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.util.Scanner;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/5 上午11:51
 */
public class AgentMain {
    /**
     * 参数添加模式 启动java主程序时添加 -javaangent:agent路径
     * premain 方法
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        //使用ByteBuddy来增强当前的类
        new AgentBuilder.Default()//模版代码
                //禁止ByteBuddy在处理时修改类名
                .disableClassFormatChanges()
                //处理时使用retransform增强
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                //打印出错误的日志
                .with(new AgentBuilder.Listener.WithTransformationsOnly(AgentBuilder.Listener.StreamWriting
                        .toSystemOut()))
                //匹配哪些类  匹配所有spring controller层的方法
                .type(ElementMatchers.isAnnotatedWith(ElementMatchers.named("org.springframework.web.bind.annotation.RestController")
                        .or(ElementMatchers.named("org.springframework.stereotype.Controller"))))
                //增强使用MyAdvice 对所有的方法都进行增强
                .transform((builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                        builder.visit(Advice
                                        .withCustomMapping()
                                        //将自定义的注解和agent参数映射上
                                        .bind(AgentParam.class,agentArgs)
                                .to(TimingAdvice.class).on(ElementMatchers.any())))
                .installOn(inst);
    }

    /**
     * attach 挂载模式 java主程序运行之后，随时可以将agent挂载上去
     */

    //-XX:+UseSerialGC -Xmx1g -Xms512m
    public static void agentmain(String agentArgs, Instrumentation inst) {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    菜单:
                    1、查看内存使用情况
                    2、生成堆内存快照信息
                    3、打印栈信息
                    4、打印类加载器
                    5、打印类源码
                    6、打印方法参数和耗时
                    7、退出
                    """);
            String input = in.nextLine();
            switch (input) {
                case "1" -> MemoryCommand.printMemory();
                case "2" -> MemoryCommand.heapDump();
                case "3" -> ThreadCommand.printThreadInfo();
                case "4" -> ClassCommand.printAllClassLoader(inst);
                case "5" -> ClassCommand.printClassSourceCode(inst);
                case "6" -> ClassCommand.enhanceClassByByteBuddy(inst);
                case "7" -> {
                    return;
                }
                default -> System.out.println("请输入正确的数字!");
            }
        }
            //ClassCommand.enhanceClassByAsm(inst);

    }
}
