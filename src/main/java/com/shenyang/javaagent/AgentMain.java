package com.shenyang.javaagent;

import java.lang.instrument.Instrumentation;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/5 上午11:51
 */
public class AgentMain {
    //permain 方法
    public static void premain(String agentArgs, Instrumentation inst){
        System.out.println("permain执行了.....");
    }

    public static void agentmain(String agentArgs, Instrumentation inst){
        System.out.println("agentMain执行了.....");
    }
}
