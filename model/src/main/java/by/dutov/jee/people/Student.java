package by.dutov.jee.people;


import by.dutov.jee.encrypt.PasswordEncryptionService;
import by.dutov.jee.group.Group;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Student extends Person {
    private List<Group> groups;
    private Map<String, List<Integer>> grades;

    {
        groups = new ArrayList<>();
        grades = new HashMap<>();
    }

    public Student withUserName(String userName){
        setUserName(userName);
        return this;
    }

    public Student withPassword(String password){
        addPassword(password, this);
        return this;
    }

    public Student withName(String name){
        setName(name);
        return this;
    }

    public Student withAge(int age){
        setAge(age);
        return this;
    }

    public Student withRole(String role){
        setRole(role);
        return this;
    }

    public void addGroups(Group... groups){
        this.groups.addAll(Arrays.asList(groups));
    }

    public void addThemeAndGrades(String name, Integer... grades){
        this.grades.put(name, Arrays.asList(grades));
    }

    @Override
    public String toString() {
        return "Student{" +
                "groups=" + groups +
                ", grades=" + grades +
                super.toString();
    }

    @Override
    public String getInfo() {
        return "Name: \"" + getName() +
                "\"<br>Age: \"" + getAge() +
                "\"<br>Role: \"" + getRole() +
                "\"<br>Group(s) №: " + groupNumbersInString() +
                "<br>Grades: " + stringOfGrades(grades);
    }

    private String groupNumbersInString(){
        String result = "";
        int count = 0;
        for (Group group:groups) {
            result += group.getNumOfGroup();
            if (count != groups.size() - 1){
                result += ", ";
            } else {
                result += ";";
            }
            count++;
        }
        return result;
    }

    private String stringOfGrades(Map<String, List<Integer>> grades){
        String result = "";
        for (String s:grades.keySet()) {
            result += "<br>&nbsp;&nbsp;&nbsp;&nbsp" + s + ": ";
            List<Integer> integers = grades.get(s);
            for (int i = 0; i < integers.size(); i++) {
                result += integers.get(i);
                if (i != integers.size() - 1){
                    result += ", ";
                } else {
                    result += ";";
                }
            }
        }
        return result;
    }
}
