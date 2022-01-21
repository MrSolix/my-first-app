package by.dutov.jee.repository.group.orm;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Student;
import by.dutov.jee.repository.group.GroupDAOInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.repository.ConstantsClass.GROUP_NOT_FOUND;

@Repository("ormGroup")
@Slf4j
public class GroupDaoSpringOrm implements GroupDAOInterface {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Group save(Group group) {
        if (group.getId() == null) {
            em.persist(group);
        } else {
            update(group.getId(), group);
        }
        return group;
    }

    @Override
    public Optional<Group> find(Integer id) {
        try {
            return Optional.ofNullable(em.find(Group.class, id));
        } catch (NoResultException e) {
            log.error(GROUP_NOT_FOUND);
            return Optional.empty();
        }
    }

    @Override
    public Group update(Integer id, Group group) {
        return em.merge(group);
    }

    @Override
    public Group remove(Group group) {
        Group naturalGroup = em.find(Group.class, group.getId());
        for (int i = 0; i < naturalGroup.getStudents().size(); i++) {
            Optional<Student> first = naturalGroup.getStudents().stream().findFirst();
            first.ifPresent(naturalGroup::removeStudent);
        }
        em.remove(naturalGroup);
        return naturalGroup;
    }

    @Override
    public List<Group> findAll() {
        TypedQuery<Group> query = em.createQuery("from Group ", Group.class);
        return query.getResultList();
    }
}
