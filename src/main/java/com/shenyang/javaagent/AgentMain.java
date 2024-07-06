package com.shenyang.javaagent;

import com.shenyang.javaagent.command.ClassCommand;
import com.shenyang.javaagent.command.MemoryCommand;
import com.shenyang.javaagent.command.ThreadCommand;

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
     * permain 方法
     *
     * @param agentArgs
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("premain执行了.....");
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
