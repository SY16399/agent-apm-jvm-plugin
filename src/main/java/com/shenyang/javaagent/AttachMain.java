package com.shenyang.javaagent;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/5 下午12:39
 */
public class AttachMain {
    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        //获取进程的虚拟机对象
        //1.执行jps命令，打印所有进程列表
        Process jps = Runtime.getRuntime().exec("jps");
        //这个对象会返回一个流
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jps.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        }
        //2.输入进程的id
        Scanner input = new Scanner(System.in);
        String processId = input.nextLine();
        VirtualMachine vm = VirtualMachine.attach(processId);
        //执行java agent里面的agentmain方法
        vm.loadAgent("E:\\WorkAndClass\\agent\\shen-agent\\target\\shen-agent-1.0-SNAPSHOT-jar-with-dependencies.jar");

    }
}
