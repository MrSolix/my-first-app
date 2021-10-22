package by.dutov.jee.people;

import by.dutov.jee.group.Group;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Data
@NoArgsConstructor
public class Teacher extends Person {
    private Group group;
    private double salary;

    public void setSalary(double salary) {
        if (salary >= 0) {
            this.salary = salary;
        } else {
            this.salary = 0;
        }
    }

    public Teacher withUserName(String userName){
        setUserName(userName);
        return this;
    }

    public Teacher withPassword(String password){
        addPassword(password, this);
        return this;
    }

    public Teacher withName(String name){
        setName(name);
        return this;
    }

    public Teacher withAge(int age){
        setAge(age);
        return this;
    }

    public Teacher withRole(String role){
        setRole(role);
        return this;
    }

    public Teacher withSalary(double salary){
        setSalary(salary);
        return this;
    }

    public Teacher withGroup(Group group){
        setGroup(group);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Teacher teacher = (Teacher) o;
        return Double.compare(teacher.salary, salary) == 0 &&
                Objects.equals(group, teacher.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), salary);
    }

    @Override
    public String getInfo() {
        return "Name: \"" + getName() +
                "\"<br>Age: \"" + getAge() +
                "\"<br>Role: \"" + getRole() +
                "\"<br>Group №: " + (group != null ? group.getNumOfGroup() : 0) +
                "<br>Salary: " + getSalary();
    }
}
