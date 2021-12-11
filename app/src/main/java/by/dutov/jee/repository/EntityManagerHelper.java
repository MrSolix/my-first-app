package by.dutov.jee.repository;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class EntityManagerHelper {
    private final SessionFactory factory;

    private EntityManagerHelper() {
        Configuration cfg = new Configuration().configure();
        factory = cfg.buildSessionFactory();
    }

    public EntityManager getEntityManager() {
        return factory.createEntityManager();
    }
}
