package com.shenyang.javaagent.command;

import com.sun.management.HotSpotDiagnosticMXBean;

import java.io.IOException;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/5 下午2:49
 */
public class MemoryCommand {

    /**
     * 打印所有内存信息
     */
    public static void printMemory() {
        //通过如下的方式获取JVM默认提供的Mbean，获取内存信息
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        //堆内存
        System.out.println("堆内存: ");
        getMemoryInfo(memoryPoolMXBeans, MemoryType.HEAP);
        //非堆内存
        System.out.println("非堆内存: ");
        getMemoryInfo(memoryPoolMXBeans, MemoryType.NON_HEAP);
        System.out.println("nio相关直接内存: ");
        //打印Nio相关的内存
        getDirectMemoryInfo();

    }

    /**
     * 生成内存快照
     */
    public static void heapDump(){
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        //HotSpot诊断Bean
        HotSpotDiagnosticMXBean hotSpotDiagnosticMXBean = ManagementFactory
                .getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        try {
            hotSpotDiagnosticMXBean.dumpHeap(simpleFormatter.format(new Date())+".hprof",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取直接内存信息
     */
    private static void getDirectMemoryInfo() {
        try {
            Class clazz = Class.forName("java.lang.management.BufferPoolMXBean");
            List<BufferPoolMXBean> bufferPoolMXBeans = ManagementFactory.getPlatformMXBeans(clazz);
            //打印内容
            for (BufferPoolMXBean bufferPoolMXBean : bufferPoolMXBeans) {
                String sb = "name:" +
                        bufferPoolMXBean.getName() +
                        " used:" +
                        bufferPoolMXBean.getMemoryUsed() / 1024 / 1024 +
                        "m" +
                        " capacity:" +
                        bufferPoolMXBean.getTotalCapacity() / 1024 / 1024 +
                        "m";
                System.out.println(sb);

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取堆内存或方法区信息
     * @param memoryPoolMXBeans 获取内存信息使用的MXBeans对象
     * @param nonHeap 选择获取方法区还是堆
     */
    private static void getMemoryInfo(List<MemoryPoolMXBean> memoryPoolMXBeans, MemoryType nonHeap) {
        memoryPoolMXBeans.stream().filter(x -> x.getType().equals(nonHeap))
                .forEach(x -> {
                    String sb = "name: " +
                            x.getName() +
                            " used: " +
                            x.getUsage().getUsed() / 1024 / 1024 +
                            "m" +
                            " committed: " +
                            x.getUsage().getCommitted() / 1024 / 1024 +
                            "m" +
                            " max: " +
                            x.getUsage().getMax() / 1024 / 1024 +
                            "m";
                    System.out.println(sb);
                });
    }
}
