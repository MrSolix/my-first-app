package by.dutov.jee.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Component("ormEntityManager")
@Slf4j
@RequiredArgsConstructor
public class SpringEntityManager extends EntityManagerHelper {

    private final EntityManagerFactory emf;

    @Override
    public EntityManager getObject() {
        EntityManager entityManager = threadLocal.get();
        if (entityManager == null) {
            entityManager = emf.createEntityManager();
            threadLocal.set(entityManager);
        }
        return entityManager;
    }
}
