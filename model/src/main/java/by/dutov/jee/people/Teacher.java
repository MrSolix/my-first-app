package by.dutov.jee.people;

import by.dutov.jee.group.Group;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Teacher extends Person {
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
//    @JsonBackReference
    private Group group;
    private double salary;

    public void setSalary(double salary) {
        if (salary >= 0) {
            this.salary = salary;
        } else {
            this.salary = 0;
        }
    }

    public Teacher withId(Integer id){
        setId(id);
        return this;
    }

    public Teacher withUserName(String userName){
        setUserName(userName);
        return this;
    }

    public Teacher withPassword(String password){
        addPassword(password, this);
        return this;
    }

    public Teacher withBytePass(byte[] pass){
        setPassword(pass);
        return this;
    }

    public Teacher withSalt(byte[] salt){
        setSalt(salt);
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

    public Teacher withRole(Role role){
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
    public String getInfo() {
        return "Name: \"" + getName() +
                "\"<br>Age: \"" + getAge() +
                "\"<br>Role: \"" + getRole() +
                "\"<br>Group №: " + (group != null ? group.getId() : 0) +
                "<br>Salary: " + getSalary();
    }
}
