package com.thinkingin.cloud.log.annotation;


import com.thinkingin.cloud.log.config.RuntimeLoggerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author wbw
 * @version 1.0
 * @date 2020/4/2 6:35 上午
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({RuntimeLoggerConfiguration.class})
public @interface EnableRuntimeLogger {
}