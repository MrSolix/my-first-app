package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.aspect.JpaTransaction;
import by.dutov.jee.group.Group;
import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.people.grades.Grade;
import by.dutov.jee.repository.DAOInterface;
import by.dutov.jee.repository.EntityManagerHelper;
import by.dutov.jee.repository.group.jpa.GroupDaoJpa;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository("jpaPerson")
public class PersonDaoJpa extends AbstractPersonDaoJpa implements DAOInterface<Person> {
    public static final String ERROR_FROM_FIND_GRADES = "Error from find grades";
    public static final String ERROR_FROM_SAVE_GRADES = "Error from save grades";
    public static final String ERROR_FROM_SAVE_GROUPS = "Error from save groups";
    public static final String ERROR_FROM_SAVE_GROUP = "Error from save group";
    public static final String ERROR_FROM_SAVE_SALARY = "Error from save salary";
    public static final String PERSON_NOT_FOUND = "Person not found";
    public static final String ERROR_FROM_REMOVE_TEACHER = "Error from remove teacher";
    public static final String REMOVE_TEACHER_FROM_GROUP = "update Group g set g.teacher = null where g.id = :id and g.teacher.id = :teacher_id";
    private Class<? extends Person> classType;
    private String findAllJpql;
    private String namedQueryByName;
    private String namedQueryById;
    private final GroupDaoJpa groupDaoJpa;

    @Autowired
    public PersonDaoJpa(EntityManagerHelper entityManagerHelper, GroupDaoJpa groupDaoJpa) {
        super(entityManagerHelper);
        this.groupDaoJpa = groupDaoJpa;
    }

    @Override
    @JpaTransaction
    public Person save(Person person) {
        return super.save(person);
    }

    @Override
    @JpaTransaction
    public Optional<Person> find(Integer id) {
        Optional<Person> student = getPerson(id, null, Role.STUDENT);
        if (student.isPresent()) {
            return student;
        }
        Optional<Person> teacher = getPerson(id, null, Role.TEACHER);
        if (teacher.isPresent()) {
            return teacher;
        }
        return getPerson(id, null, Role.ADMIN);
    }

    private Optional<Person> getPerson(Integer id, String name, Role personRole) {
        setParameters(personRole);
        if (id != null) {
            return super.find(id);
        } else {
            return super.find(name);
        }
    }

    @Override
    @JpaTransaction
    public Optional<Person> find(String name) {
        Optional<Person> student = getPerson(null, name, Role.STUDENT);
        if (student.isPresent()) {
            return student;
        }
        Optional<Person> teacher = getPerson(null, name, Role.TEACHER);
        if (teacher.isPresent()) {
            return teacher;
        }
        return getPerson(null, name, Role.ADMIN);
    }

    @Override
    @JpaTransaction
    public Person remove(Person person) {
        EntityManager em = helper.getEntityManager();
        if (Role.STUDENT.equals(person.getRole())) {
            return removeStudent(em, (Student) person);
        }
        if (Role.TEACHER.equals(person.getRole())) {
            return removeTeacher((Teacher) person);
        }
        throw new DataBaseException(PERSON_NOT_FOUND);
    }

    private Teacher removeTeacher(Teacher teacher) {
        EntityManager em = helper.getEntityManager();
        Group group = teacher.getGroup();
        if (group != null) {
            Query query = em.createQuery(REMOVE_TEACHER_FROM_GROUP);
            query.setParameter("id", group.getId());
            query.setParameter("teacher_id", teacher.getId());
            query.executeUpdate();
            teacher.removeGroup(group);
        }
        return (Teacher) super.remove(teacher);
    }

    private Student removeStudent(EntityManager em, Student student) {
        for (int i = 0; i < student.getGroups().size(); i++) {
            Optional<Group> first = student.getGroups().stream().findFirst();
            first.ifPresent(student::removeGroup);
        }
        for (int i = 0; i < student.getGrades().size(); i++) {
            Grade grade = student.getGrades().get(i);
            em.remove(grade);
        }
        return (Student) super.remove(student);
    }

    @Override
    @JpaTransaction
    public Person update(Integer id, Person person) {
        EntityManager em = helper.getEntityManager();
        if (Role.STUDENT.equals(person.getRole())) {
            Optional<Person> oldStudent = getPerson(id, null, Role.STUDENT);
            if (oldStudent.isPresent()) {
                Student student = updateStudent(((Student) oldStudent.get()), ((Student) person));
                em.merge(student);
                return student;
            }
        }
        if (Role.TEACHER.equals(person.getRole())) {
            Optional<Person> oldTeacher = getPerson(id, null, Role.TEACHER);
            if (oldTeacher.isPresent()) {
                Teacher teacher = updateTeacher(((Teacher) oldTeacher.get()), ((Teacher) person));
                em.merge(teacher);
                return teacher;
            }
        }
        throw new DataBaseException(PERSON_NOT_FOUND);
    }

    private Teacher updateTeacher(Teacher oldTeacher, Teacher teacher) {
        Group group = teacher.getGroup();
        Double salary = teacher.getSalary();
        if (group != null) {
            saveGroup(oldTeacher, teacher);
        }
        if (salary != null) {
            saveSalary(oldTeacher, teacher);
        }
        return oldTeacher;
    }

    private void saveSalary(Teacher oldTeacher, Teacher teacher) {
        Double oldSalary = oldTeacher.getSalary();
        Double newSalary = teacher.getSalary();
        if (newSalary != null && !oldSalary.equals(newSalary)) {
            oldTeacher.setSalary(newSalary);
        }
    }

    private void saveGroup(Teacher oldTeacher, Teacher teacher) {
        Group newGroup = teacher.getGroup();
        Group oldGroup = oldTeacher.getGroup();
        Optional<Group> group = groupDaoJpa.find(newGroup.getId());
        if (group.isPresent() && group.get().getTeacher() == null) {
            if (oldGroup != null) {
                oldGroup.setTeacher(null);
            }
            oldTeacher.setGroup(newGroup);
        }
    }

    private Student updateStudent(Student oldStudent, Student student) {
        Set<Group> groups = student.getGroups();
        List<Grade> grades = student.getGrades();
        if (groups != null && !groups.isEmpty()) {
            saveGroups(oldStudent, groups);
        }
        if (grades != null && !grades.isEmpty()) {
            saveGrades(oldStudent, grades);
        }
        return oldStudent;
    }

    private void saveGroups(Student oldStudent, Set<Group> studentGroups) {
        Set<Group> oldGroups = oldStudent.getGroups();
        if (!studentGroups.isEmpty()) {
            for (Group g : studentGroups) {
                if (!oldGroups.contains(g)) {
                    Optional<Group> group = groupDaoJpa.find(g.getId());
                    group.ifPresent(oldStudent::addGroup);
                }
            }
        }
    }

    private void saveGrades(Student oldStudent, List<Grade> studentGrades) {
        List<Grade> oldGrades = oldStudent.getGrades();
        List<Grade> allGrades = new ArrayList<>(oldGrades);
        allGrades.removeAll(studentGrades);
        allGrades.addAll(studentGrades);
        if (allGrades.isEmpty()) {
            return;
        }
        List<Grade> updateGrades = equalsGradeLists(oldGrades, allGrades);
        List<Grade> newGrades = checkNewGrades(allGrades, updateGrades);
        oldStudent.setGrades(newGrades);
    }

    private List<Grade> checkNewGrades(List<Grade> allGrades, List<Grade> updateGrades) {
        List<Grade> result = new ArrayList<>();
        allGrades.removeAll(updateGrades);
        for (Grade allGrade : allGrades) {
            Grade newGrade = new Grade();
            if (allGrade.getThemeName() != null) {
                newGrade.setThemeName(allGrade.getThemeName());
            } else {
                newGrade.setThemeName("Math");
            }
            if (allGrade.getGrade() != null) {
                newGrade.setGrade(allGrade.getGrade());
            } else {
                newGrade.setGrade(0);
            }
            result.add(newGrade);
        }
        result.addAll(updateGrades);
        return result;
    }

    private List<Grade> equalsGradeLists(List<Grade> oldGrades, List<Grade> allGrades) {
        List<Grade> result = new ArrayList<>();
        Map<Integer, Grade> mapGrades = new HashMap<>();
        oldGrades.forEach(grade -> mapGrades.putIfAbsent(grade.getId(), grade));
        for (Grade allGrade : allGrades) {
            if (oldGrades.contains(allGrade)) {
                Grade oldGrade = mapGrades.get(allGrade.getId());
                Grade newGrade = new Grade();
                newGrade.setId(oldGrade.getId());
                if (allGrade.getThemeName() != null) {
                    newGrade.setThemeName(allGrade.getThemeName());
                } else {
                    newGrade.setThemeName(oldGrade.getThemeName());
                }
                if (allGrade.getGrade() != null) {
                    newGrade.setGrade(allGrade.getGrade());
                } else {
                    newGrade.setGrade(oldGrade.getGrade());
                }
                result.add(newGrade);
            }
        }
        return result;
    }

    @Override
    @JpaTransaction
    public List<Person> findAll() {
        setParameters(Role.STUDENT);
        List<Person> personList = new ArrayList<>(super.findAll());
        setParameters(Role.TEACHER);
        personList.addAll(super.findAll());
        return personList;
    }

    @Override
    protected Class<? extends Person> getType() {
        return classType;
    }

    @Override
    protected String findAllJpql() {
        return findAllJpql;
    }

    @Override
    protected String namedQueryByName() {
        return namedQueryByName;
    }

    @Override
    protected String namedQueryById() {
        return namedQueryById;
    }

    private void setParameters(Role role) {
        switch (role) {
            case STUDENT:
                classType = Student.class;
                findAllJpql = "from Student u where u.role = 'STUDENT'";
                namedQueryByName = "findStudentByName";
                namedQueryById = "findStudentById";
                return;
            case TEACHER:
                classType = Teacher.class;
                findAllJpql = "from Teacher u where u.role = 'TEACHER'";
                namedQueryByName = "findTeacherByName";
                namedQueryById = "findTeacherById";
                return;
            case ADMIN:
                classType = Admin.class;
                findAllJpql = "from Admin u where u.role = 'ADMIN'";
                namedQueryByName = "findAdminByName";
                namedQueryById = "findAdminById";
        }
    }
}
