package by.dutov.jee.people;


import by.dutov.jee.group.Group;
import by.dutov.jee.people.grades.Grade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "users")
@Entity
@NamedQuery(name = "findStudentByName", query = "from Student u where u.userName = :name and u.role = 'STUDENT'")
@NamedQuery(name = "findStudentById", query = "from Student u where u.id = :id and u.role = 'STUDENT'")
@NamedQuery(name = "findAllStudents", query = "from Student u where u.role = 'STUDENT'")
public class Student extends Person {
    @ToString.Include
    @EqualsAndHashCode.Include
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "group_student",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups;
    @ToString.Include
    @EqualsAndHashCode.Include
    @OneToMany(
            mappedBy = "student",
            cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true
    )
    private List<Grade> grades;

    {
        setRole(Role.STUDENT);
    }

    public Student withGroups(Set<Group> groups) {
        setGroups(groups);
        return this;
    }

    public Student withId(Integer id) {
        setId(id);
        return this;
    }

    public Student withUserName(String userName) {
        setUserName(userName);
        return this;
    }

    public Student withPassword(String password) {
        addPassword(password, this);
        return this;
    }

    public Student withBytePass(byte[] pass) {
        setPassword(pass);
        return this;
    }

    public Student withSalt(byte[] salt) {
        setSalt(salt);
        return this;
    }

    public Student withName(String name) {
        setName(name);
        return this;
    }

    public Student withAge(int age) {
        setAge(age);
        return this;
    }

    public Student withRole(Role role) {
        setRole(role);
        return this;
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
        group.getStudents().remove(this);
    }

    public Student withGrades(List<Grade> grades) {
        setGrades(grades);
        return this;
    }

    public void setGrades(List<Grade> grades) {
        if (grades != null) {
            this.grades.clear();
            this.grades.addAll(grades);
            grades.forEach(grade -> grade.setStudent(this));
        }
    }

    public void addGrade(Grade grade) {
        grades.add(grade);
        grade.setStudent(this);
    }

    public void removeGrade(Grade grade) {
        grades.remove(grade);
        grade.setStudent(null);
    }

    @Override
    public String infoGet() {
        return "Name: \"" + getName() +
                "\"<br>Age: \"" + getAge() +
                "\"<br>Role: \"" + getRole() +
                "\"<br>Group(s) №: " + groupNumbersInString() +
                "<br>Grades: " + stringOfGrades(grades);
    }

    private String groupNumbersInString() {
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (Group group : groups) {
            result.append(group.getId());
            if (count != groups.size() - 1) {
                result.append(", ");
            } else {
                result.append(";");
            }
            count++;
        }
        return result.toString();
    }

    private String stringOfGrades(List<Grade> grades) {
        StringBuilder result = new StringBuilder();
        Map<String, List<Integer>> map = new LinkedHashMap<>();
        for (Grade grade : grades) {
            map.putIfAbsent(grade.getThemeName(), new ArrayList<>());
            map.get(grade.getThemeName()).add(grade.getGrade());
        }
        Set<String> strings = map.keySet();
        for (String s : strings) {
            result.append("<br>&nbsp;&nbsp;&nbsp;&nbsp").append(s).append(": ").append(map.get(s));
        }
        return result.toString();
    }


}
