package com.shenyang.command;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/5 下午4:01
 */
public class ThreadCommand {


    /**
     * 获取线程运行信息
     */
    public static void printThreadInfo(){
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        //当前监视器是否支持，当前同步器是否支持。
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(threadMXBean.isObjectMonitorUsageSupported(), threadMXBean.isSynchronizerUsageSupported());
        //打印线程信息
        for (ThreadInfo threadInfo : threadInfos) {
            StringBuilder sb = new StringBuilder();
            sb.append("name: ")
                    .append(threadInfo.getThreadName())
                    .append(" threadId: ")
                    .append(threadInfo.getThreadId())
                    .append(" threadState: ")
                    .append(threadInfo.getThreadState());
            System.out.println(sb);
            //打印栈信息
            StackTraceElement[] stackTrace = threadInfo.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                System.out.println(stackTraceElement);
            }
        }
        
    }
}
