package com.shenyang.javaagent;

import com.shenyang.command.ClassCommand;

import java.lang.instrument.Instrumentation;

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
        //MemoryCommand.printMemory();
        //MemoryCommand.heapDump();
        //ThreadCommand.printThreadInfo();
        //ClassCommand.printAllClassLoader(inst);
        ClassCommand.printClassSourceCode(inst);
    }
}
