package by.dutov.jee.repository.person.orm;

import by.dutov.jee.group.Group;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.people.grades.Grade;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class StudentDaoSpringOrm extends AbstractPersonDaoSpringOrm {



    public StudentDaoSpringOrm() {
        clazz = Student.class;
    }

    public Student update(Student oldStudent, Student student) {
        Set<Group> groups = student.getGroups();
        List<Grade> grades = student.getGrades();
        if (groups != null && !groups.isEmpty()) {
            saveGroups(oldStudent, groups);
        }
        if (grades != null && !grades.isEmpty()) {
            saveGrades(oldStudent, grades);
        }
        return em.merge(oldStudent);
    }

    private void saveGroups(Student oldStudent, Set<Group> studentGroups) {
        Set<Group> oldGroups = oldStudent.getGroups();
        if (!studentGroups.isEmpty()) {
            for (Group g : studentGroups) {
                if (!oldGroups.contains(g)) {
                    Optional<Group> group = groupDaoSpringOrm.find(g.getId());
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
    public Person remove(Person person) {
        Student student = (Student) person;
        for (int i = 0; i < student.getGroups().size(); i++) {
            Optional<Group> first = student.getGroups().stream().findFirst();
            first.ifPresent(student::removeGroup);
        }
        for (int i = 0; i < student.getGrades().size(); i++) {
            Grade grade = student.getGrades().get(i);
            em.remove(grade);
        }
        return super.remove(student);
    }

    @Override
    protected String findAllJpql() {
        return "from Student u where u.role = 'STUDENT'";
    }

    @Override
    protected String namedQueryByName() {
        return "findStudentByName";
    }

    @Override
    protected String namedQueryById() {
        return "findStudentById";
    }
}
