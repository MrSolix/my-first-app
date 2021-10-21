package by.dutov.jee.people;

import by.dutov.jee.encrypt.PasswordEncryptionService;
import by.dutov.jee.group.Group;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Data
@EqualsAndHashCode(callSuper = true)
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

    @Override
    public String toString() {
        return "Teacher{" +
                "groupId=" + group.getNumOfGroup() +
                "\n salary=" + getSalary() +
                "\n" + super.toString() +
                '}';
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
