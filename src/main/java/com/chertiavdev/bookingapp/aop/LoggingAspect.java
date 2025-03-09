package com.chertiavdev.bookingapp.aop;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("com.chertiavdev.bookingapp.aop.CommonPointcuts.controllerLog()")
    public void doBeforeController(JoinPoint joinPoint) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.info("HTTP Request: Method={}, URL={}, HandlerMethod={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    joinPoint.getSignature().toShortString());
        }
    }

    @AfterReturning(
            value = "com.chertiavdev.bookingapp.aop.CommonPointcuts.controllerLog()",
            returning = "result",
            argNames = "joinPoint,result")
    public void doAfterReturningController(JoinPoint joinPoint, Object result) {
        log.info("Controller method executed successfully: {}. Arguments: {}. Response: {}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()),
                sanitizeResult(result));
    }

    @AfterThrowing(
            throwing = "exception",
            pointcut = "com.chertiavdev.bookingapp.aop.CommonPointcuts.controllerLog()"
    )
    public void throwsException(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.error("Exception in {}.{} with arguments {}. Exception message: {}",
                className,
                methodName,
                Arrays.toString(joinPoint.getArgs()),
                exception.getMessage());
    }

    @Around("com.chertiavdev.bookingapp.aop.CommonPointcuts.controllerLog()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.info("Execution method: {}.{}. Execution time: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                executionTime);

        return proceed;
    }

    @Before("com.chertiavdev.bookingapp.aop.CommonPointcuts.serviceLog()")
    public void doBeforeService(JoinPoint joinPoint) {
        log.info("Service method {}.{} was called. Arguments: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(
            value = "com.chertiavdev.bookingapp.aop.CommonPointcuts.serviceLog()",
            returning = "result",
            argNames = "joinPoint,result")
    public void doAfterReturningService(JoinPoint joinPoint, Object result) {
        log.info("Service method {}.{} executed successfully, result {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                sanitizeResult(result));
    }

    @AfterThrowing(
            pointcut = "com.chertiavdev.bookingapp.aop.CommonPointcuts.serviceLog()",
            throwing = "exception"
    )
    public void handleException(JoinPoint joinPoint, Exception exception) {
        log.error("Service method {}.{} threw exception: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getMessage());
    }

    private Object sanitizeResult(Object result) {
        if (result == null) {
            return null;
        }
        if (result instanceof String) {
            return maskJwtToken((String) result);
        }
        if (result.toString().contains("token=")) {
            return result.toString().replaceAll("(token=)[^,)}]+",
                    "$1[PROTECTED]");
        }
        return result;
    }

    private String maskJwtToken(String input) {
        if (input.toLowerCase().contains("token")) {
            return input.replaceAll("(token=)[^,)}]+", "$1[PROTECTED]");
        }
        return input;
    }
}
