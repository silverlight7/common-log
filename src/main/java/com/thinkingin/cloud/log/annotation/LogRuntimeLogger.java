package com.thinkingin.cloud.log.annotation;

import java.lang.annotation.*;

/**
 * @author wbw
 * @version 1.0
 * @date 2020/4/2 6:36 上午
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LogRuntimeLogger {

    String topic() default "";

}
