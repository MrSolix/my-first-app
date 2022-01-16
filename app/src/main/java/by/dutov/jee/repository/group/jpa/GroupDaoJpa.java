package by.dutov.jee.repository.group.jpa;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Student;
import by.dutov.jee.repository.EntityManagerHelper;
import by.dutov.jee.repository.group.GroupDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;


@Slf4j
@Repository("jpaGroup")
@Lazy
public class GroupDaoJpa implements GroupDAOInterface {
    public static final String ERROR_FROM_REMOVE = "Error from remove";
    public static final String ERROR_FROM_UPDATE = "Error from update";
    public static final String ERROR_FROM_SAVE = "Error from save";
    public static final String ERROR_FROM_FIND = "Error from find";
    public static final String ERROR_FROM_FIND_ALL = "Error from findAll";
    protected final EntityManagerHelper helper;

    @Autowired
    public GroupDaoJpa(EntityManagerHelper jpaEntityManager) {
        this.helper = jpaEntityManager;
    }


    @Override
    public Group save(Group group) {
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            if (group.getId() == null) {
                em.persist(group);
            } else {
                update(group.getId(), group);
            }

            helper.commitSingle(em);
            return group;
        } catch (Exception e) {
            helper.rollBack(em);
            log.error(ERROR_FROM_SAVE);
            throw new DataBaseException(ERROR_FROM_SAVE, e);
        } finally {
            helper.closeQuietly(em);
        }
    }

    @Override
    public Optional<Group> find(Integer id) {
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            Group entity = em.find(Group.class, id);

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
    public Group update(Integer id, Group group) {
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            em.merge(group);

            helper.commitSingle(em);
            return group;
        } catch (Exception e) {
            helper.rollBack(em);
            log.error(ERROR_FROM_UPDATE);
            throw new DataBaseException(ERROR_FROM_UPDATE);
        } finally {
            helper.closeQuietly(em);
        }
    }

    @Override
    public Group remove(Group group) {
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            Group naturalGroup = em.find(Group.class, group.getId());
            for (int i = 0; i < naturalGroup.getStudents().size(); i++) {
                Optional<Student> first = naturalGroup.getStudents().stream().findFirst();
                first.ifPresent(naturalGroup::removeStudent);
            }

            em.remove(naturalGroup);

            helper.commitSingle(em);
            return naturalGroup;
        } catch (Exception e) {
            helper.rollBack(em);
            log.error(ERROR_FROM_REMOVE);
            throw new DataBaseException(ERROR_FROM_REMOVE);
        } finally {
            helper.closeQuietly(em);
        }
    }

    @Override
    public List<Group> findAll() {
        List<Group> entities;
        EntityManager em = null;
        try {
            em = helper.getObject();
            helper.begin(em);

            TypedQuery<Group> query = em.createQuery("from Group ", Group.class);
            entities = query.getResultList();

            helper.commitSingle(em);
        } catch (Exception e) {
            helper.rollBack(em);
            log.error(ERROR_FROM_FIND_ALL);
            throw new DataBaseException(ERROR_FROM_FIND_ALL);
        } finally {
            helper.closeQuietly(em);
        }
        return entities;
    }
}
