package by.dutov.jee;

import by.dutov.jee.dao.PersonDAOImpl;
import by.dutov.jee.group.Group;
import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;

public class InitializeClass {
    public static PersonDAOImpl personDAO;
    public static final int CURRENT_MONTH = LocalDate.now().getMonthValue();
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_ADMIN = "ADMIN";

    static {
        personDAO = new PersonDAOImpl();
        initUsers();
    }

    private static void initUsers() {
        Student student = new Student("student", "123",
                "Vasya", 20, ROLE_STUDENT);

        student.addThemeAndGrades("Math", 9,7,6,8);
        student.addThemeAndGrades("English", 6,5,7,4,8);

        Student student1 = new Student("student1", "123",
                "Gena", 22, ROLE_STUDENT);

        student1.addThemeAndGrades("Math", 5,4,6,7);
        student1.addThemeAndGrades("English", 9,7,8,6,9);


        Teacher teacher = new Teacher("teacher", "123",
                "Peter", 34, ROLE_TEACHER);
        Teacher teacher1 = new Teacher("teacher1", "123",
                "Daniel", 45, ROLE_TEACHER);

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


        Admin admin = new Admin("admin", "123",
                "Administrator", 90, ROLE_ADMIN);

        Group group = new Group(13, teacher, Arrays.asList(student, student1));


        personDAO.create(student.getUserName(), student);
        personDAO.create(student1.getUserName(), student1);
        personDAO.create(teacher.getUserName(), teacher);
        personDAO.create(teacher1.getUserName(), teacher1);
        personDAO.create(admin.getUserName(), admin);

    }
}
