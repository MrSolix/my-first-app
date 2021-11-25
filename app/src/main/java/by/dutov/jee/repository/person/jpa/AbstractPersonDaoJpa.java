package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.repository.EntityManagerHelper;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.utils.DataBaseUtils.closeQuietly;
import static by.dutov.jee.utils.DataBaseUtils.rollBack;


@Slf4j
public abstract class AbstractPersonDaoJpa<T extends Person> implements PersonDAOInterface<T> {
    public static final String ERROR_FROM_REMOVE = "Error from remove";
    public static final String ERROR_FROM_UPDATE = "Error from update";
    public static final String ERROR_FROM_SAVE = "Error from save";
    public static final String ERROR_FROM_FIND = "Error from find";
    public static final String ERROR_FROM_FIND_ALL = "Error from findAll";
    protected final EntityManagerHelper helper = EntityManagerHelper.getInstance();


    @Override
    public T save(T t) {
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
    public Optional<? extends Person> find(Integer id) {
        T entity;
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            entity = em.find(getType(), id);

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
    public Optional<? extends Person> find(String name) {
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            TypedQuery<T> find = em.createNamedQuery(nameNamedQuery(), getType());
            find.setParameter("name", name);
            T entity = find.getSingleResult();

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
    public T update(Integer id, T t) {
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
    public T remove(T t) {
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
    public List<? extends Person> findAll() {
        List<T> entities;
        EntityManager em = null;
        try {
            em = helper.getEntityManager();
            em.getTransaction().begin();

            TypedQuery<T> query = em.createQuery(findAllJpql(), getType());
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

    protected abstract Class<T> getType();

    protected abstract String findAllJpql();

    protected abstract String nameNamedQuery();

}
