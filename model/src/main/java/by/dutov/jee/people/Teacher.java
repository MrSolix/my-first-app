package by.dutov.jee.people;

import by.dutov.jee.group.Group;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
public class Teacher extends Person {
    @ToString.Include
    @EqualsAndHashCode.Include
    @OneToOne(cascade = {CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.DETACH,
            CascadeType.REFRESH},
            mappedBy = "teacher")
    private Group group;
    private double salary;

    {
        setRole(Role.TEACHER);
    }

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
