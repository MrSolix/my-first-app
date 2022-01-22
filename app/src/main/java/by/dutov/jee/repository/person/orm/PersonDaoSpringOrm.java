package by.dutov.jee.repository.person.orm;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static by.dutov.jee.repository.ConstantsClass.PERSON_NOT_FOUND;

@Repository("ormPerson")
@RequiredArgsConstructor
public class PersonDaoSpringOrm implements PersonDAOInterface {

    private final StudentDaoSpringOrm studentDaoSpringOrm;
    private final TeacherDaoSpringOrm teacherDaoSpringOrm;
    private final AdminDaoSpringOrm adminDaoSpringOrm;

    @Override
    public Person save(Person person) {
        if (Role.STUDENT.equals(person.getRole())) {
            return studentDaoSpringOrm.save(person);
        }
        if (Role.TEACHER.equals(person.getRole())) {
            return teacherDaoSpringOrm.save(person);
        }
        return adminDaoSpringOrm.save(person);
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
        Optional<Person> optionalPerson = find(id);
        if (optionalPerson.isPresent()) {
            Person oldPerson = optionalPerson.get();
            if (Role.STUDENT.equals(oldPerson.getRole())) {
                return studentDaoSpringOrm.update(((Student) oldPerson), ((Student) person));
            }
            if (Role.TEACHER.equals(oldPerson.getRole())) {
                return teacherDaoSpringOrm.update(((Teacher) oldPerson), ((Teacher) person));
            }
        }
        throw new DataBaseException(PERSON_NOT_FOUND);
    }

    @Override
    public Person remove(Person person) {
        if (Role.STUDENT.equals(person.getRole())) {
            return studentDaoSpringOrm.remove(person);
        }
        if (Role.TEACHER.equals(person.getRole())) {
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
