package com.thinkingin.cloud.log.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.thinkingin.cloud.log.annotation.LogRuntimeLogger;
import com.thinkingin.cloud.log.annotation.LogTag;
import com.thinkingin.cloud.log.model.RuntimeInformation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author wbw
 * @version 1.0
 * @date 2020/4/2 6:38 上午
 */


@Aspect
public class RuntimeLoggerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeLoggerConfiguration.class);

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public RuntimeLoggerConfiguration() {
        LOGGER.info("Initializing Runtime Logger Component");
    }

    @Around("execution(* com.art.artserviceorder..*Controller.*(..))")
    Object logRuntimeInformation(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Method method = Arrays.stream(joinPoint.getSignature().getDeclaringType().getDeclaredMethods()).filter(m -> m.getName().equals(joinPoint.getSignature().getName())).findFirst().get();
        RuntimeInformation runtimeInformation = null;
        try {
            // class enable to log runtime log
            if (method.getDeclaringClass().isAnnotationPresent(LogRuntimeLogger.class)) {
                runtimeInformation = new RuntimeInformation();
                LogTag logTag = method.getAnnotation(LogTag.class);
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

                runtimeInformation.setRequestParams(JSONObject.toJSONString(joinPoint.getArgs()));
                runtimeInformation.setTag(logTag != null ? logTag.value() : LogTag.DEFAULT_TAG);
                runtimeInformation.setApplicationName(this.applicationName);
                runtimeInformation.setClassName(method.getDeclaringClass().getName());
                runtimeInformation.setMethod(method.getName());
                runtimeInformation.setRequestUrl(request != null ? request.getRequestURL().toString() : "NONE");
                runtimeInformation.setRequestMethod(request != null ? request.getMethod() : "NONE");
                if (request != null && request.getCookies() != null) {
                    runtimeInformation.setCookies(String.join("\r\n", Arrays.stream(request.getCookies()).reduce(new ArrayList<String>(), (cookieList, cookie) -> {
                        cookieList.add(MessageFormat.format("Name: {0}, Value: {1}, Domain: {2}, Path: {3}, Secure: {4}, Max Age: {5}, Version: {6}",
                                cookie.getName(),
                                cookie.getValue(),
                                cookie.getDomain(),
                                cookie.getPath(),
                                cookie.getSecure(),
                                cookie.getMaxAge(),
                                cookie.getVersion()));
                        return cookieList;
                    }, (a, b) -> new ArrayList<>())));
                } else {
                    runtimeInformation.setCookies("NONE");
                }
            }
            result = joinPoint.proceed();
        } catch (Throwable t) {
            if (runtimeInformation != null) {
                runtimeInformation.setExceptionMessage(t.getMessage());
                runtimeInformation.setExceptionStack(String.join("\r\n", Arrays.stream(t.getStackTrace()).reduce(new ArrayList<String>(), (stackList, stack) -> {
                    stackList.add(stack.toString());
                    return stackList;
                }, (a, b) -> new ArrayList<>())));
            }
            throw t;
        } finally {
            if (runtimeInformation != null) {
                runtimeInformation.setResponseResult(JSONObject.toJSONString(result));
                runtimeInformation.complete();
                String topic = method.getDeclaringClass().getAnnotation(LogRuntimeLogger.class).topic();

                if (!topic.isEmpty()) {
                    kafkaTemplate.send(topic, JSONObject.toJSONStringWithDateFormat(runtimeInformation, "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat));
                }
            }
        }
        return result;
    }

}