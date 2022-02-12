package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.aspect.JpaTransaction;
import by.dutov.jee.auth.Role;
import by.dutov.jee.people.Person;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.repository.ConstantsClass.ERROR_FROM_SAVE;
import static by.dutov.jee.repository.ConstantsClass.PERSON_NOT_FOUND;

@Slf4j
@Repository("jpaPerson")
@RequiredArgsConstructor
public class PersonDaoJpa implements PersonDAOInterface {
    private final StudentDaoJpa studentDaoJpa;
    private final TeacherDaoJpa teacherDaoJpa;
    private final AdminDaoJpa adminDaoJpa;

    @Override
    @JpaTransaction
    public Person save(Person person) {
        if (person.getRolesName(person.getRoles()).contains(Role.ROLE_STUDENT)) {
            return studentDaoJpa.save(person);
        }
        if (person.getRolesName(person.getRoles()).contains(Role.ROLE_TEACHER)) {
            return teacherDaoJpa.save(person);
        }
        log.error(ERROR_FROM_SAVE);
        throw new DataBaseException(ERROR_FROM_SAVE);
    }

    @Override
    @JpaTransaction
    public Optional<Person> find(Integer id) {
        Optional<Person> student = studentDaoJpa.find(id);
        if (student.isPresent()) {
            return student;
        }
        Optional<Person> teacher = teacherDaoJpa.find(id);
        if (teacher.isPresent()) {
            return teacher;
        }
        return adminDaoJpa.find(id);
    }

    @Override
    @JpaTransaction
    public Optional<Person> find(String name) {
        Optional<Person> student = studentDaoJpa.find(name);
        if (student.isPresent()) {
            return student;
        }
        Optional<Person> teacher = teacherDaoJpa.find(name);
        if (teacher.isPresent()) {
            return teacher;
        }
        return adminDaoJpa.find(name);
    }

    @Override
    @JpaTransaction
    public Person remove(Person person) {
        if (person.getRolesName(person.getRoles()).contains(Role.ROLE_STUDENT)) {
            return studentDaoJpa.remove(person);
        }
        if (person.getRolesName(person.getRoles()).contains(Role.ROLE_TEACHER)) {
            return teacherDaoJpa.remove(person);
        }
        throw new DataBaseException(PERSON_NOT_FOUND);
    }

    @Override
    @JpaTransaction
    public Person update(Integer id, Person person) {
        try {
            return studentDaoJpa.update(id, person);
        } catch (DataBaseException e) {
            return teacherDaoJpa.update(id, person);
        }
    }

    @Override
    @JpaTransaction
    public List<Person> findAll() {
        List<Person> result = new ArrayList<>();
        result.addAll(studentDaoJpa.findAll());
        result.addAll(teacherDaoJpa.findAll());
        return result;
    }
}
