package com.wootecobook.turkey.commons.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static Logger log;

    @Pointcut("execution(* com.wootecobook.turkey.*.controller..*Controller.*(..))")
    public void loggingForController() {}

    @Pointcut("execution(* com.wootecobook.turkey.*.*Advice.*(..))")
    public void loggingForException() {
    }

    @Around("loggingForController()")
    public Object controllerLogging(final ProceedingJoinPoint pjp) throws Throwable {
        setLogger(pjp.getSignature().getDeclaringType());
        log.info("request by {}, args: {} ", pjp.getSignature(), pjp.getArgs());
        Object requestResult = pjp.proceed();
        log.info("response {}", requestResult);

        return requestResult;
    }

    @Around("loggingForException() && args(exception)")
    public Object exceptionLogging(final ProceedingJoinPoint pjp, Exception exception) throws Throwable {
        setLogger(exception.getClass());
        Object result = pjp.proceed();
        log.error("errorMessage: {}", exception.getMessage());

        return result;
    }

    private void setLogger(Class<?> clazz) {
        log = LoggerFactory.getLogger(clazz);
    }
}
