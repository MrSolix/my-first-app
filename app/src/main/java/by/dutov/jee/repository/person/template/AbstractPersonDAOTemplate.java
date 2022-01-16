package by.dutov.jee.repository.person.template;

import by.dutov.jee.aspect.TemplateTransaction;
import by.dutov.jee.group.Group;
import by.dutov.jee.people.Person;
import by.dutov.jee.repository.ConstantsClass;
import by.dutov.jee.repository.group.postgres.GroupDAOPostgres;
import by.dutov.jee.repository.group.template.GroupDaoTemplate;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import by.dutov.jee.service.group.GroupDaoInstance;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static by.dutov.jee.repository.ConstantsClass.DELETE_USER_BY_ID;
import static by.dutov.jee.repository.ConstantsClass.INSERT_USER;
import static by.dutov.jee.repository.ConstantsClass.SELECT_USER_BY_ID_AND_ROLE;
import static by.dutov.jee.repository.ConstantsClass.SELECT_USER_BY_NAME_AND_ROLE;
import static by.dutov.jee.repository.ConstantsClass.SELECT_USER_BY_ROLE;
import static by.dutov.jee.repository.ConstantsClass.UPDATE_USER_BY_ID;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractPersonDAOTemplate implements PersonDAOInterface {

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    protected RowMapper<Person> rowMapper;
    @Autowired
    protected GroupDaoTemplate groupDaoTemplate;

    @Override
    @Transactional
    public Optional<Person> find(String name) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_USER_BY_NAME_AND_ROLE, rowMapper, name, setRole()));
        } catch (EmptyResultDataAccessException e) {
            log.error("Person is not found");
            return Optional.empty();
        } catch (Exception e2) {
            log.error("Error from 'find' method in the 'Template' class", e2);
            throw new DataBaseException("Error from 'find' method in the 'Template' class", e2);
        }
    }

    @Override
    @Transactional
    public Optional<Person> find(Integer id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_USER_BY_ID_AND_ROLE, rowMapper, id, setRole()));
        } catch (EmptyResultDataAccessException e) {
            log.error("Person is not found");
            return Optional.empty();
        } catch (Exception e2) {
            log.error("Error from 'find' method in the 'Template' class", e2);
            throw new DataBaseException("Error from 'find' method in the 'Template' class", e2);
        }
    }

    @Override
    @Transactional
    public Person save(Person person) {
        try {
            Integer id = jdbcTemplate.queryForObject(INSERT_USER, (rs, rowNum) ->
                            rs.getInt("id"),
                    person.getUserName(),
                    person.getPassword(),
                    person.getSalt(),
                    person.getName(),
                    person.getAge(),
                    person.getRole().getType()
            );
            person.setId(id);
            return person;
        } catch (Exception e) {
            log.error("Error from 'save' method in the 'Template' class", e);
            throw new DataBaseException("Error from 'save' method in the 'Template' class", e);
        }
    }

    @Override
    @Transactional
    public Person update(Integer id, Person person) {
        try {
            int update = jdbcTemplate.update(UPDATE_USER_BY_ID, person.getUserName(), person.getPassword(), person.getSalt(),
                    person.getName(), person.getAge(), person.getRole().getType(), id);
            if (update > 0) {
                return person;
            }
            throw new DataBaseException("update == 0");
        } catch (Exception e) {
            log.error("Error from 'update' method in the 'Template' class", e);
            throw new DataBaseException("Error from 'update' method in the 'Template' class", e);
        }
    }

    @Override
    @Transactional
    public Person remove(Person person) {
        try {
            int update = jdbcTemplate.update(DELETE_USER_BY_ID, person.getId());
            if (update > 0) {
                return person;
            }
            throw new DataBaseException("update == 0");
        } catch (Exception e) {
            log.error("Error from 'remove' method in the 'Template' class", e);
            throw new DataBaseException("Error from 'remove' method in the 'Template' class", e);
        }
    }

    @Override
    @Transactional
    public List<Person> findAll() {
        try {
            return jdbcTemplate.query(SELECT_USER_BY_ROLE, rowMapper, setRole());
        } catch (Exception e) {
            log.error("Error from 'findAll' method in the 'Template' class", e);
            throw new DataBaseException("Error from 'findAll' method in the 'Template' class", e);
        }
    }

    protected abstract String setRole();

    protected abstract Optional<Person> getUser(String name, Integer id);
    protected abstract Set<Group> getGroup(Integer userId);
}
