package by.dutov.jee.repository.person.orm;

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

@Repository("ormPerson")
@RequiredArgsConstructor
@Slf4j
public class PersonDaoSpringOrm implements PersonDAOInterface {

    private final StudentDaoSpringOrm studentDaoSpringOrm;
    private final TeacherDaoSpringOrm teacherDaoSpringOrm;
    private final AdminDaoSpringOrm adminDaoSpringOrm;

    @Override
    public Person save(Person person) {
        if (person.getRolesName(person.getRoles()).contains(Role.ROLE_STUDENT)) {
            return studentDaoSpringOrm.save(person);
        }
        if (person.getRolesName(person.getRoles()).contains(Role.ROLE_TEACHER)) {
            return teacherDaoSpringOrm.save(person);
        }
        log.error(ERROR_FROM_SAVE);
        throw new DataBaseException(ERROR_FROM_SAVE);
    }

    @Override
    public Optional<Person> find(String name) {
        Optional<Person> student = studentDaoSpringOrm.find(name);
        if (student.isPresent()) {
            return student;
        }
        Optional<Person> teacher = teacherDaoSpringOrm.find(name);
        if (teacher.isPresent()) {
            return teacher;
        }
        return adminDaoSpringOrm.find(name);
    }

    @Override
    public Optional<Person> find(Integer id) {
        Optional<Person> student = studentDaoSpringOrm.find(id);
        if (student.isPresent()) {
            return student;
        }
        Optional<Person> teacher = teacherDaoSpringOrm.find(id);
        if (teacher.isPresent()) {
            return teacher;
        }
        return adminDaoSpringOrm.find(id);
    }

    @Override
    public Person update(Integer id, Person person) {
        try {
            return studentDaoSpringOrm.update(id, person);
        } catch (DataBaseException e) {
            return teacherDaoSpringOrm.update(id, person);
        }
    }

    @Override
    public Person remove(Person person) {
        if (person.getRolesName(person.getRoles()).contains(Role.ROLE_STUDENT)) {
            return studentDaoSpringOrm.remove(person);
        }
        if (person.getRolesName(person.getRoles()).contains(Role.ROLE_TEACHER)) {
            return teacherDaoSpringOrm.remove(person);
        }
        throw new DataBaseException(PERSON_NOT_FOUND);
    }

    @Override
    public List<Person> findAll() {
        List<Person> result = new ArrayList<>();
        result.addAll(studentDaoSpringOrm.findAll());
        result.addAll(teacherDaoSpringOrm.findAll());
        return result;
    }
}
