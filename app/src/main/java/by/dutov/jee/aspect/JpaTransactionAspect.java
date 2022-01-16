package by.dutov.jee.aspect;

import by.dutov.jee.repository.EntityManagerHelper;
import by.dutov.jee.repository.person.postgres.ConnectionType;
import by.dutov.jee.service.AbstractDaoInstance;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Optional;

import static by.dutov.jee.repository.AbstractGeneralTransaction.connectionType;

@Aspect
@Slf4j
@Component
public class JpaTransactionAspect extends AbstractDaoInstance {

    private final String SUFFIX = "EntityManager";
    private EntityManagerHelper helper;
    private final Map<String, EntityManagerHelper> emMap;

    public JpaTransactionAspect(Map<String, EntityManagerHelper> emMap) {
        this.emMap = emMap;
    }

    @PostConstruct
    private void init() {
        helper = emMap.get(repositoryType + SUFFIX);
    }

    @SneakyThrows
    @Around("@annotation(JpaTransaction)")
    public Object transaction(ProceedingJoinPoint jp) {
        String methodName = jp.getSignature().getName();
        log.info("Transaction : {}", methodName);
        Object result;
        connectionType = ConnectionType.MANY;
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            result = jp.proceed();

            if (result instanceof Optional) {
               if (((Optional) result).isEmpty()) {
                    return Optional.empty();
                }
            }
            helper.commitMany(em);
        } catch (Exception e) {
            helper.rollBack(em);
            log.error("Error from " + methodName);
            throw new DataBaseException("Error from " + methodName, e);
        } finally {
            helper.close(em);
        }
        return result;
    }
}
