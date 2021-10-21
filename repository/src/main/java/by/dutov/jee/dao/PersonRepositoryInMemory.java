package by.dutov.jee.dao;

import by.dutov.jee.Finance;
import by.dutov.jee.encrypt.PasswordEncryptionService;
import by.dutov.jee.exceptions.HashException;
import by.dutov.jee.group.Group;
import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersonRepositoryInMemory implements DAO<Person> {

    private static PersonRepositoryInMemory instance;
    private Map<String, Person> accounts = new ConcurrentHashMap<>();
    public static final int CURRENT_MONTH = LocalDate.now().getMonthValue();
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_ADMIN = "ADMIN";

    public PersonRepositoryInMemory() {
    }

    public static PersonRepositoryInMemory getInstance(){
        if (instance == null){
            synchronized (PersonRepositoryInMemory.class){
                if (instance == null){
                    instance = new PersonRepositoryInMemory();
                }
            }
        }
        return instance;
    }

    @Override
    public void create(String name, Person person) {
        accounts.put(name, person);
    }

    @Override
    public Person read(String name) {
        return accounts.get(name);
    }

    @Override
    public Person read(String name, String password) {
        Person person = accounts.get(name);
        PasswordEncryptionService instance = PasswordEncryptionService.getInstance();
        try {
            if (person != null && instance.authenticate(password, person.getPassword(), person.getSalt())){
                return person;
            }
        } catch (HashException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(String name, Person person) {
        accounts.replace(name, person);
    }

    @Override
    public void delete(String name) {
        accounts.remove(name);
    }

    {
        Student student = new Student()
                .withUserName("student")
                .withPassword("123")
                .withName("Vasya")
                .withAge(21)
                .withRole(ROLE_STUDENT);

        student.addThemeAndGrades("Math", 9,7,6,8);
        student.addThemeAndGrades("English", 6,5,7,4,8);

        Student student1 = new Student()
                .withUserName("student1")
                .withPassword("123")
                .withUserName("Gena")
                .withAge(22)
                .withRole(ROLE_STUDENT);

        student1.addThemeAndGrades("Math", 5,4,6,7);
        student1.addThemeAndGrades("English", 9,7,8,6,9);

        Teacher teacher = new Teacher()
                .withUserName("teacher")
                .withPassword("123")
                .withName("Peter")
                .withAge(34)
                .withRole(ROLE_TEACHER);

        Teacher teacher1 = new Teacher()
                .withUserName("teacher1")
                .withPassword("123")
                .withName("Daniel")
                .withAge(45)
                .withRole(ROLE_TEACHER);

        Group group = new Group()
                .withNumOfGroup(13)
                .withTeacher(teacher1)
                .withStudents(Arrays.asList(student, student1));

        Group group1 = new Group()
                .withNumOfGroup(7)
                .withTeacher(teacher)
                .withStudents(Arrays.asList(student, student1));

        teacher.setSalary(5000);
        teacher1.setSalary(3000);
        Finance.salaryHistory.put(teacher, new HashMap<>());
        Finance.salaryHistory.put(teacher1, new HashMap<>());
        for (int i = 1; i < CURRENT_MONTH; i++) {
            Finance.salaryHistory.get(teacher).put(i, i * 110.0);
            Finance.salaryHistory.get(teacher1).put(i, i * 100.0);
        }
        Finance.salaryHistory.get(teacher).put(CURRENT_MONTH, teacher.getSalary());
        Finance.salaryHistory.get(teacher1).put(CURRENT_MONTH, teacher1.getSalary());


        Admin admin = new Admin()
                .withUserName("admin")
                .withPassword("123")
                .withName("Administrator")
                .withAge(90)
                .withRole(ROLE_ADMIN);


        create(student.getUserName(), student);
        create(student1.getUserName(), student1);
        create(teacher.getUserName(), teacher);
        create(teacher1.getUserName(), teacher1);
        create(admin.getUserName(), admin);
    }
}
