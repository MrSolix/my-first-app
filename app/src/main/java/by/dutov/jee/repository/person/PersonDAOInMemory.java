package by.dutov.jee.repository.person;

import by.dutov.jee.service.encrypt.PasswordEncryptionService;
import by.dutov.jee.service.exceptions.HashException;
import by.dutov.jee.group.Group;
import by.dutov.jee.people.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class PersonDAOInMemory extends PersonDAO<Person> {

    private static volatile PersonDAOInMemory instance;
    private Map<Integer, Person> accounts;

    public PersonDAOInMemory() {
        accounts = new ConcurrentHashMap<>();
        //singleton
    }

    public static PersonDAOInMemory getInstance() {
        if (instance == null) {
            synchronized (PersonDAOInMemory.class) {
                if (instance == null) {
                    instance = new PersonDAOInMemory();
                }
            }
        }
        return instance;
    }

    @Override
    void sqlForFind(String sql) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Person save(Person person) {
        if (person != null) {
            accounts.put(person.getId(), person);
            return accounts.get(person.getId());
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

    public Optional<Person> find(String name, String password) {
        for (Person person : accounts.values()) {
            if (person.getUserName().equals(name)) {
                try {
                    if (PasswordEncryptionService.getInstance().authenticate(password, person.getPassword(), person.getSalt())) {
                        return Optional.of(person);
                    }
                } catch (HashException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Optional.empty();
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

    @Override
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

    @Override
    String[] aliases() {
        return new String[0];
    }

    {
        Student student = new Student()
                .withId(1)
                .withUserName("student")
                .withPassword("123")
                .withName("Vasya")
                .withAge(21)
                .withRole(Role.STUDENT);

        student.addThemeAndGrades("Math", 9, 7, 6, 8);
        student.addThemeAndGrades("English", 6, 5, 7, 4, 8);

        Student student1 = new Student()
                .withId(2)
                .withUserName("student1")
                .withPassword("123")
                .withName("Gena")
                .withAge(22)
                .withRole(Role.STUDENT);

        student1.addThemeAndGrades("Math", 5, 4, 6, 7);
        student1.addThemeAndGrades("English", 9, 7, 8, 6, 9);

        Teacher teacher = new Teacher()
                .withId(3)
                .withUserName("teacher")
                .withPassword("123")
                .withName("Peter")
                .withAge(34)
                .withSalary(4000)
                .withRole(Role.TEACHER);

        Teacher teacher1 = new Teacher()
                .withId(4)
                .withUserName("teacher1")
                .withPassword("123")
                .withName("Daniel")
                .withAge(45)
                .withSalary(2000)
                .withRole(Role.TEACHER);

        Group group = new Group()
                .withId(1)
                .withTeacher(teacher1)
                .withStudents(Arrays.asList(student, student1));

        Group group1 = new Group()
                .withId(2)
                .withTeacher(teacher)
                .withStudents(Arrays.asList(student, student1));

        Admin admin = new Admin()
                .withId(5)
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
