package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.people.Person;
import by.dutov.jee.repository.EntityManagerHelper;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.repository.ConstantsClass.ERROR_FROM_FIND;
import static by.dutov.jee.repository.ConstantsClass.ERROR_FROM_FIND_ALL;
import static by.dutov.jee.repository.ConstantsClass.ERROR_FROM_REMOVE;
import static by.dutov.jee.repository.ConstantsClass.ERROR_FROM_SAVE;
import static by.dutov.jee.repository.ConstantsClass.ERROR_FROM_UPDATE;

@Slf4j
public abstract class AbstractPersonDaoJpa implements PersonDAOInterface {
    protected final EntityManagerHelper helper;

    public AbstractPersonDaoJpa(EntityManagerHelper entityManagerHelper) {
        this.helper = entityManagerHelper;
    }

    @Override
    public Person save(Person person) {
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            if (person.getId() == null) {
                em.persist(person);
            } else {
                update(person.getId(), person);
            }

            helper.commitSingle(em);
            return person;
        } catch (Exception e) {
            helper.rollBack(em);
            log.error(ERROR_FROM_SAVE);
            throw new DataBaseException(ERROR_FROM_SAVE, e);
        } finally {
            helper.closeQuietly(em);
        }
    }

    @Override
    public Optional<Person> find(Integer id) {
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            TypedQuery<? extends Person> find = em.createNamedQuery(namedQueryById(), getType());
            find.setParameter("id", id);
            Person entity = find.getSingleResult();

            helper.commitSingle(em);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            helper.rollBack(em);
            log.error(ERROR_FROM_FIND);
            return Optional.empty();
        } finally {
            helper.closeQuietly(em);
        }
    }

    @Override
    public Optional<Person> find(String name) {
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            TypedQuery<? extends Person> find = em.createNamedQuery(namedQueryByName(), getType());
            find.setParameter("name", name);
            Person entity = find.getSingleResult();

            helper.commitSingle(em);
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            helper.rollBack(em);
            log.error(ERROR_FROM_FIND);
            return Optional.empty();
        } finally {
            helper.closeQuietly(em);
        }
    }

    @Override
    public Person update(Integer id, Person person) {
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            em.merge(person);

            helper.commitSingle(em);
            return person;
        } catch (Exception e) {
            helper.rollBack(em);
            log.error(ERROR_FROM_UPDATE);
            throw new DataBaseException(ERROR_FROM_UPDATE);
        } finally {
            helper.closeQuietly(em);
        }
    }

    @Override
    public Person remove(Person person) {
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            em.remove(person);

            helper.commitSingle(em);
            return person;
        } catch (Exception e) {
            helper.rollBack(em);
            log.error(ERROR_FROM_REMOVE);
            throw new DataBaseException(ERROR_FROM_REMOVE);
        } finally {
            helper.closeQuietly(em);
        }
    }

    @Override
    public List<Person> findAll() {
        List<Person> entities;
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            TypedQuery<Person> query = em.createQuery(findAllJpql(), Person.class);
            entities = query.getResultList();

            helper.commitSingle(em);
        } catch (Exception e) {
            helper.rollBack(em);
            log.error(ERROR_FROM_FIND_ALL);
            return new ArrayList<>();
        } finally {
            helper.closeQuietly(em);
        }
        return entities;
    }

    protected abstract Class<? extends Person> getType();

    protected abstract String findAllJpql();

    protected abstract String namedQueryByName();

    protected abstract String namedQueryById();

}
