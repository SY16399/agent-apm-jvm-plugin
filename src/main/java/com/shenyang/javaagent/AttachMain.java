package com.shenyang.javaagent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/5 下午12:39
 */
public class AttachMain {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        //获取进程的虚拟机对象
        VirtualMachine vm = VirtualMachine.attach("12120");
        //执行java agent里面的agentmain方法
        vm.loadAgent("E:\\WorkAndClass\\agent\\shen-agent\\target\\shen-agent-1.0-SNAPSHOT-jar-with-dependencies.jar");

    }
}
