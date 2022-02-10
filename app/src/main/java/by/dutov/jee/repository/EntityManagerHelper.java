package by.dutov.jee.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Component("jpaEntityManager")
@Slf4j
@RequiredArgsConstructor
public class EntityManagerHelper extends AbstractGeneralTransaction<EntityManager> {
    private final EntityManagerFactory factory;

    @Override
    public EntityManager getObject() {
        EntityManager entityManager = threadLocal.get();
        if (entityManager == null) {
            entityManager = factory.createEntityManager();
            threadLocal.set(entityManager);
        }
        return entityManager;
    }

    public void begin(EntityManager entityManager) {
        if (entityManager.getTransaction().isActive()) {
            return;
        }
        entityManager.getTransaction().begin();
    }

    @Override
    public void commitSingle(EntityManager entityManager) {
        if (ConnectionType.SINGLE.equals(connectionType)) {
            entityManager.getTransaction().commit();
        }
    }

    @Override
    public void commitMany(EntityManager entityManager) {
        entityManager.getTransaction().commit();
        connectionType = ConnectionType.SINGLE;
    }

    @Override
    public void rollBack(EntityManager entityManager) {
        if (entityManager == null) {
            return;
        }
        if (entityManager.getTransaction() != null) {
            entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void remove() {
        if (threadLocal.get() != null) {
            threadLocal.remove();
        }
    }

    @Override
    public void close(EntityManager entityManager) {
        try {
            if (entityManager != null) {
                entityManager.close();
                remove();
            }
        } catch (Exception e) {
            log.error("Couldn't close and remove connection", e);
        }
    }

    public void closeQuietly(EntityManager entityManager) {
        try {
            if (entityManager != null && ConnectionType.SINGLE.equals(connectionType)) {
                entityManager.close();
            }
        } catch (Exception e) {
            log.error("Couldn't close ", e);
        }
    }
}
