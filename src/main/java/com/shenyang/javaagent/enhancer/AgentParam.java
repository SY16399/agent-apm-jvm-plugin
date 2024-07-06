package com.shenyang.javaagent.enhancer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/6 下午10:24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AgentParam {
    String value();
}
