package by.dutov.jee.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//@Aspect
@Slf4j
@Component
public class LoggingAspect {

    @Pointcut("execution(* by.dutov.jee.controllers.servlets.*.*.*(..)) || execution(* by.dutov.jee.controllers.servlets.*.*(..))")
    public void controllers() {}

    @Before("controllers()")
    public void beforeLogging(JoinPoint jp) {
        log.info("Entry into the \"" + jp.getSignature().getName() +"\" method");
    }

    @After("controllers()")
    public void afterLogging(JoinPoint jp) {
        log.info("Exit from method \"" + jp.getSignature().getName() + "\"");
    }
}
