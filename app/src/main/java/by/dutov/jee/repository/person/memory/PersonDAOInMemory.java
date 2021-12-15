package by.dutov.jee.repository.person.memory;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Admin;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.person.PersonDAOInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Repository("memoryPerson")
@Lazy
public class PersonDAOInMemory implements PersonDAOInterface<Person> {
    private static Integer ID = 1;
    private Integer id;
    private final Map<Integer, Person> accounts;

    @Autowired
    private PersonDAOInMemory() {
        accounts = new ConcurrentHashMap<>();
    }

    @Override
    public Person save(Person person) {
        if (person != null) {
            accounts.put(person.getId(), person);
            return accounts.get(id);
        }
        return null;
    }

    @Override
    public Optional<? extends Person> find(String name) {
        for (Person person : accounts.values()) {
            if (person.getUserName().equals(name)) {
                return Optional.of(person);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Person> find(Integer id) {
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public Person update(Integer id, Person person) {
        Optional<? extends Person> oldPerson = find(id);
        return oldPerson.map(value -> accounts.replace(value.getId(), person)).orElse(null);
    }

    @Override
    public Person remove(Person person) {
        Optional<? extends Person> person1 = find(person.getUserName());
        if (person1.isPresent()) {
            accounts.remove(person1.get().getId());
            return person;
        }
        return null;
    }


    public List<? extends Person> findAll(Role role) {
        return accounts.values()
                .stream()
                .filter(value -> value.getRole().equals(role))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Person> findAll() {
        return accounts.isEmpty() ? new ArrayList<>() : new ArrayList<>(accounts.values());
    }

    @PostConstruct
    private void initialize() {
        Student student = new Student()
                .withId(id=ID++)
                .withUserName("student")
                .withPassword("123")
                .withName("Vasya")
                .withAge(21)
                .withRole(Role.STUDENT);
        List<Grade> grades = new ArrayList<>();
        grades.add(new Grade()
                .withName("Math")
                .withGrade(9));
        grades.add(new Grade()
                .withName("Math")
                .withGrade(7));
        grades.add(new Grade()
                .withName("Math")
                .withGrade(6));
        grades.add(new Grade()
                .withName("Math")
                .withGrade(8));
        grades.add(new Grade()
                .withName("English")
                .withGrade(6));
        grades.add(new Grade()
                .withName("English")
                .withGrade(5));
        grades.add(new Grade()
                .withName("English")
                .withGrade(7));
        grades.add(new Grade()
                .withName("English")
                .withGrade(4));
        grades.add(new Grade()
                .withName("English")
                .withGrade(8));
        student.setGrades(grades);


        Student student1 = new Student()
                .withId(id=ID++)
                .withUserName("student1")
                .withPassword("123")
                .withName("Gena")
                .withAge(22)
                .withRole(Role.STUDENT);

        List<Grade> grade1 = new ArrayList<>();
        grade1.add(new Grade()
                .withName("Math")
                .withGrade(5));
        grade1.add(new Grade()
                .withName("Math")
                .withGrade(4));
        grade1.add(new Grade()
                .withName("Math")
                .withGrade(6));
        grade1.add(new Grade()
                .withName("Math")
                .withGrade(7));
        grade1.add(new Grade()
                .withName("English")
                .withGrade(9));
        grade1.add(new Grade()
                .withName("English")
                .withGrade(7));
        grade1.add(new Grade()
                .withName("English")
                .withGrade(8));
        grade1.add(new Grade()
                .withName("English")
                .withGrade(6));
        grade1.add(new Grade()
                .withName("English")
                .withGrade(9));


        student1.setGrades(grade1);


        Teacher teacher = new Teacher()
                .withId(id=ID++)
                .withUserName("teacher")
                .withPassword("123")
                .withName("Peter")
                .withAge(34)
                .withSalary(4000)
                .withRole(Role.TEACHER);

        Teacher teacher1 = new Teacher()
                .withId(id=ID++)
                .withUserName("teacher1")
                .withPassword("123")
                .withName("Daniel")
                .withAge(45)
                .withSalary(2000)
                .withRole(Role.TEACHER);

        Group group = new Group()
                .withId(1)
                .withTeacher(teacher1)
                .withStudents(Set.of(student, student1));

        Group group1 = new Group()
                .withId(2)
                .withTeacher(teacher)
                .withStudents(Set.of(student, student1));

        Admin admin = new Admin()
                .withId(id=ID++)
                .withUserName("admin")
                .withPassword("123")
                .withName("Administrator")
                .withAge(90)
                .withRole(Role.ADMIN);


        save(student);
        save(student1);
        save(teacher);
        save(teacher1);
        save(admin);
    }
}
