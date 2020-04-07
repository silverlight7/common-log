package com.thinkingin.cloud.log.annotation;

import java.lang.annotation.*;

/**
 * @author wbw
 * @version 1.0
 * @date 2020/4/2 6:37 上午
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LogTag {

    public static final String DEFAULT_TAG = "DEFAULT_TAG";

    String value() default DEFAULT_TAG;

}
