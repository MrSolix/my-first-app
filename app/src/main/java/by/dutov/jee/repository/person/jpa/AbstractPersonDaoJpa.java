package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.people.Person;
import by.dutov.jee.repository.EntityManagerHelper;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;


@Slf4j
public abstract class AbstractPersonDaoJpa implements PersonDAOInterface {
    public static final String ERROR_FROM_REMOVE = "Error from remove";
    public static final String ERROR_FROM_UPDATE = "Error from update";
    public static final String ERROR_FROM_SAVE = "Error from save";
    public static final String ERROR_FROM_FIND = "Error from find";
    public static final String ERROR_FROM_FIND_ALL = "Error from findAll";
    protected final EntityManagerHelper helper;

    public AbstractPersonDaoJpa(EntityManagerHelper entityManagerHelper) {
        this.helper = entityManagerHelper;
    }

    @Override
    public Person save(Person t) {
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            if (t.getId() == null) {
                em.persist(t);
            } else {
                update(t.getId(), t);
            }

            em.getTransaction().commit();
            return t;
        } catch (Exception e) {
            rollBack(em);
            log.error(ERROR_FROM_SAVE);
            throw new DataBaseException(ERROR_FROM_SAVE, e);
        } finally {
            closeQuietly(em);
        }
    }

    @Override
    public Optional<Person> find(Integer id) {
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            TypedQuery<? extends Person> find = em.createNamedQuery(namedQueryById(), getType());
            find.setParameter("id", id);
            Person entity = find.getSingleResult();

            em.getTransaction().commit();
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            rollBack(em);
            log.error(ERROR_FROM_FIND);
            throw new DataBaseException(ERROR_FROM_FIND, e);
        } finally {
            closeQuietly(em);
        }
    }

    @Override
    public Optional<Person> find(String name) {
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            TypedQuery<? extends Person> find = em.createNamedQuery(namedQueryByName(), getType());
            find.setParameter("name", name);
            Person entity = find.getSingleResult();

            em.getTransaction().commit();
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            rollBack(em);
            log.error(ERROR_FROM_FIND);
            throw new DataBaseException(ERROR_FROM_FIND, e);
        } finally {
            closeQuietly(em);
        }
    }

    @Override
    public Person update(Integer id, Person t) {
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            em.merge(t);

            em.getTransaction().commit();
            return t;
        } catch (Exception e) {
            rollBack(em);
            log.error(ERROR_FROM_UPDATE);
            throw new DataBaseException(ERROR_FROM_UPDATE);
        } finally {
            closeQuietly(em);
        }
    }

    @Override
    public Person remove(Person t) {
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            em.remove(t);

            em.getTransaction().commit();
            return t;
        } catch (Exception e) {
            rollBack(em);
            log.error(ERROR_FROM_REMOVE);
            throw new DataBaseException(ERROR_FROM_REMOVE);
        } finally {
            closeQuietly(em);
        }
    }

    @Override
    public List<Person> findAll() {
        List<Person> entities;
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            TypedQuery<Person> query = em.createQuery(findAllJpql(), Person.class);
            entities = query.getResultList();

            em.getTransaction().commit();
        } catch (Exception e) {
            rollBack(em);
            log.error(ERROR_FROM_FIND_ALL);
            throw new DataBaseException(ERROR_FROM_FIND_ALL);
        } finally {
            closeQuietly(em);
        }
        return entities;
    }

    protected abstract Class<? extends Person> getType();

    protected abstract String findAllJpql();

    protected abstract String namedQueryByName();

    protected abstract String namedQueryById();

}
