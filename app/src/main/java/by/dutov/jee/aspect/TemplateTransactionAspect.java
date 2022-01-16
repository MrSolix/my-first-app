package by.dutov.jee.aspect;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Slf4j
@Component
public class TemplateTransactionAspect {

    @SneakyThrows
    @Around("@annotation(TemplateTransaction)")
    public Object transaction(ProceedingJoinPoint jp) {
        String methodName = jp.getSignature().getName();
        log.info("Transaction : {}", methodName);
        Object result;
        try {
            result = jp.proceed();
        }catch (EmptyResultDataAccessException e) {
            log.error("Person is not found");
            return Optional.empty();
        }
        return result;
    }
}
