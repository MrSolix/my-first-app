package by.dutov.jee.people;

import java.util.Objects;

public class Teacher extends Person {
    private long numOfGroup;
    private double salary;

    public Teacher(String userName, String password, String name, int age, String role) {
        super(userName, password, name, age, role);
    }


    public long getNumOfGroup() {
        return numOfGroup;
    }

    public void setNumOfGroup(long numOfGroup) {
        this.numOfGroup = numOfGroup;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if (salary >= 0) {
            this.salary = salary;
        } else {
            this.salary = 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Teacher teacher = (Teacher) o;
        return numOfGroup == teacher.numOfGroup &&
                Objects.equals(salary, teacher.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), numOfGroup, salary);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "groupId=" + numOfGroup +
                "\n salary=" + getSalary() +
                "\n" + super.toString() +
                '}';
    }

    @Override
    public String getInfo() {
        return "Name: \"" + getName() +
                "\"<br>Age: \"" + getAge() +
                "\"<br>Role: \"" + getRole() +
                "\"<br>Group №: " + getNumOfGroup() +
                "<br>Salary: " + getSalary();
    }
}
