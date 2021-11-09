package by.dutov.jee.people;


import by.dutov.jee.group.Group;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Student extends Person {
    @ToString.Exclude
//    @JsonBackReference
    private List<Group> groups;
    private Map<String, List<Integer>> grades;

    {
        groups = new ArrayList<>();
        grades = new HashMap<>();
    }

    public Student withGroups(List<Group> groups){
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

    public Student addGroup(Group group) {
        if (!groups.contains(group)) {
            groups.add(group);
        }
        return this;
    }

    public Student withGrades(Map<String, List<Integer>> grades) {
        this.grades = grades;
        return this;
    }

    public void addThemeAndGrades(String name, Integer... grades) {
        this.grades.put(name, Arrays.asList(grades));
    }

    public Student addGrade(String name, Integer grade) {
        if (this.grades.containsKey(name)) {
            this.grades.get(name).add(grade);
        } else {
            this.grades.put(name, Arrays.asList(grade));
        }
        return this;
    }

    @Override
    public String getInfo() {
        return "Name: \"" + getName() +
                "\"<br>Age: \"" + getAge() +
                "\"<br>Role: \"" + getRole() +
                "\"<br>Group(s) №: " + groupNumbersInString() +
                "<br>Grades: " + stringOfGrades(grades);
    }

    private String groupNumbersInString() {
        String result = "";
        int count = 0;
        for (Group group : groups) {
            result += group.getId();
            if (count != groups.size() - 1) {
                result += ", ";
            } else {
                result += ";";
            }
            count++;
        }
        return result;
    }

    private String stringOfGrades(Map<String, List<Integer>> grades) {
        String result = "";
        for (String s : grades.keySet()) {
            result += "<br>&nbsp;&nbsp;&nbsp;&nbsp" + s + ": ";
            List<Integer> integers = grades.get(s);
            for (int i = 0; i < integers.size(); i++) {
                result += integers.get(i);
                if (i != integers.size() - 1) {
                    result += ", ";
                } else {
                    result += ";";
                }
            }
        }
        return result;
    }


}
