package by.dutov.jee.repository.person.orm;

import by.dutov.jee.people.Person;
import by.dutov.jee.repository.ConstantsClass;
import by.dutov.jee.repository.group.orm.GroupDaoSpringOrm;
import by.dutov.jee.repository.person.PersonDAOInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.repository.ConstantsClass.PERSON_NOT_FOUND;

@Repository
@Slf4j
public abstract class AbstractPersonDaoSpringOrm implements PersonDAOInterface {

    @Autowired
    protected GroupDaoSpringOrm groupDaoSpringOrm;
    protected Class<? extends Person> clazz;
    @PersistenceContext
    protected EntityManager em;

    @Override
    public Optional<Person> find(String name) {
        try {
            TypedQuery<? extends Person> find = em.createNamedQuery(namedQueryByName(), clazz);
            find.setParameter("name", name);
            return Optional.ofNullable(find.getSingleResult());
        } catch (NoResultException e) {
            log.error(PERSON_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Person save(Person person) {
        if (person.getId() == null) {
            em.persist(person);
        } else {
            update(person.getId(), person);
        }
        return person;
    }

    @Override
    public Optional<Person> find(Integer id) {
        try {
            TypedQuery<? extends Person> find = em.createNamedQuery(namedQueryById(), clazz);
            find.setParameter("id", id);
            return Optional.ofNullable(find.getSingleResult());
        } catch (NoResultException e) {
            log.error(PERSON_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Person update(Integer id, Person person) {
        return em.merge(person);
    }

    @Override
    public Person remove(Person person) {
        em.remove(person);
        return person;
    }

    @Override
    public List<Person> findAll() {
        TypedQuery<Person> query = em.createNamedQuery(findAllJpql(), Person.class);
        return query.getResultList();
    }

    protected abstract String findAllJpql();

    protected abstract String namedQueryByName();

    protected abstract String namedQueryById();
}
