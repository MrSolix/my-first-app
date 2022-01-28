package by.dutov.jee.repository.person.template;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("templatePerson")
@RequiredArgsConstructor
public class PersonDAOTemplate implements PersonDAOInterface {

    private final StudentDAOTemplate studentDAOTemplate;
    private final TeacherDAOTemplate teacherDAOTemplate;
    private final AdminDAOTemplate adminDAOTemplate;

    @Override
    @Transactional
    public Optional<Person> find(String name) {
        Optional<Person> student = studentDAOTemplate.find(name);
        if (student.isPresent()) {
            return student;
        }
        Optional<Person> teacher = teacherDAOTemplate.find(name);
        if (teacher.isPresent()) {
            return teacher;
        }
        return adminDAOTemplate.find(name);
    }

    @Override
    @Transactional
    public Person save(Person person) {
        if (person.getRole().equals(Role.STUDENT)){
            return person.getId() == null ? studentDAOTemplate.save(person) : update(person.getId(), person);
        }
        return person.getId() == null ? teacherDAOTemplate.save(person) : update(person.getId(), person);
    }

    @Override
    @Transactional
    public Optional<Person> find(Integer id) {
        Optional<Person> student = studentDAOTemplate.find(id);
        if (student.isPresent()) {
            return student;
        }
        Optional<Person> teacher = teacherDAOTemplate.find(id);
        if (teacher.isPresent()) {
            return teacher;
        }
        return adminDAOTemplate.find(id);
    }

    @Override
    @Transactional
    public Person update(Integer id, Person person) {
        Optional<Person> oldPerson = find(id);
        if (oldPerson.isPresent()) {
            Person newPerson = setPersonFields(oldPerson.get(), person);
            if (Role.STUDENT.equals(newPerson.getRole())) {
                Student student = (Student) newPerson;
                studentDAOTemplate.update(id, student);
                return studentDAOTemplate.updateStudent(((Student) oldPerson.get()), student, ((Student) person));
            }
            Teacher teacher = (Teacher) newPerson;
            teacherDAOTemplate.update(id, teacher);
            return teacherDAOTemplate.updateTeacher(teacher, ((Teacher) person));
        }
        throw new DataBaseException("Person not found.");
    }
    private Person setPersonFields(Person oldPerson, Person person) {
        String userName = person.getUserName();
        byte[] password = person.getPassword();
        byte[] salt = person.getSalt();
        String name = person.getName();
        Integer age = person.getAge();
        if (userName != null) {
            oldPerson.setUserName(userName);
        }
        if (password != null) {
            oldPerson.setPassword(password);
        }
        if (salt != null) {
            oldPerson.setSalt(salt);
        }
        if (name != null) {
            oldPerson.setName(name);
        }
        if (age != null) {
            oldPerson.setAge(age);
        }
        return oldPerson;
    }

    @Override
    @Transactional
    public Person remove(Person person) {
        if (Role.STUDENT.equals(person.getRole())) {
            return studentDAOTemplate.remove(person);
        }
        return teacherDAOTemplate.remove(person);
    }

    @Override
    @Transactional
    public List<Person> findAll() {
        List<Person> result = new ArrayList<>();
        List<Person> allStudents = studentDAOTemplate.findAll();
        if (!allStudents.isEmpty()){
            result.addAll(allStudents);
        }
        List<Person> allTeachers = teacherDAOTemplate.findAll();
        if (!allTeachers.isEmpty()){
            result.addAll(allTeachers);
        }
        List<Person> allAdmins = adminDAOTemplate.findAll();
        if (!allAdmins.isEmpty()) {
            result.addAll(allAdmins);
        }
        return result;
    }
}
