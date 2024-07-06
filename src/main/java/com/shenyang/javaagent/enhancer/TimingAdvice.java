package com.shenyang.javaagent.enhancer;

import net.bytebuddy.asm.Advice;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/6 下午3:43
 * 统计耗时打印方法名
 */
public class TimingAdvice {

    /**
     * 方法进入时，打印所有参数
     *
     * @return 开始时间
     */
    @Advice.OnMethodEnter
    static long enter() {
        return System.nanoTime();
    }


    /**
     * 方法退出时，统计方法执行的耗时
     *
     * @param fileName   agent参数提供的文件名
     * @param value      方法进入时的返回值
     * @param className  类名
     * @param methodName 方法名
     */
    @Advice.OnMethodExit
    static void exit(
            //默认值 apm.log
            @AgentParam("apm.log") String fileName,
            @Advice.Enter long value,
            @Advice.Origin("#t") String className,
            @Advice.Origin("#m") String methodName) {
        String str = methodName + "@" + className + "耗时为: " + (System.nanoTime() - value) + "纳秒\n";
        try {
            FileUtils.writeStringToFile(new File(fileName), str, StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
