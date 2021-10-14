package by.dutov.jee.people;


import java.util.*;

public class Student extends Person {
    private Set<Integer> groupNumbers;
    private Map<String, List<Integer>> grades;

    {
        groupNumbers = new HashSet<>();
        grades = new HashMap<>();
    }

    public Student(String login, String password, String name, int age, String role) {
        super(login, password, name, age, role);
    }

    public Set<Integer> getGroupNumbers() {
        return groupNumbers;
    }

    public void setGroupNumbers(Integer... groupNumbers) {
        this.groupNumbers.addAll(Arrays.asList(groupNumbers));
    }

    public void addThemeAndGrades(String name, Integer... grades){
        this.grades.put(name, Arrays.asList(grades));
    }

    public Map<String, List<Integer>> getGrades() {
        return grades;
    }

    public void setGroupNumbers(Set<Integer> groupNumbers) {
        this.groupNumbers = groupNumbers;
    }

    public void setGrades(Map<String, List<Integer>> grades) {
        this.grades = grades;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return Objects.equals(groupNumbers, student.groupNumbers) &&
                Objects.equals(grades, student.grades);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), groupNumbers, grades);
    }

    @Override
    public String toString() {
        return "Student{" +
                "groups=" + groupNumbers +
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
        for (int i:groupNumbers) {
            result += i;
            if (count != groupNumbers.size() - 1){
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
