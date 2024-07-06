package com.shenyang.javaagent.enhancer;

import net.bytebuddy.asm.Advice;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/6 下午3:43
 * 编写一个类似AOP的advice通知
 */
public class MyAdvice {

    /**
     * 方法进入时，打印所有参数
     *
     * @param args 方法参数集合
     * @return 开始时间
     */
    @Advice.OnMethodEnter
    static long enter(@Advice.AllArguments Object[] args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                System.out.println("参数: " + i + " 内容:" + args[i]);
            }
        }
        return System.nanoTime();
    }


    /**
     * 方法退出时，统计方法执行的耗时
     *
     * @param value 方法进入时的返回值
     */
    @Advice.OnMethodExit
    static void exit(@Advice.Enter long value) {
        System.out.println("耗时为: " + (System.nanoTime() - value)+"纳秒");
    }


}
