package by.dutov.jee.people;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Admin extends Person{

    public Admin withUserName(String userName){
        setUserName(userName);
        return this;
    }

    public Admin withPassword(String password){
        addPassword(password, this);
        return this;
    }

    public Admin withName(String name){
        setName(name);
        return this;
    }

    public Admin withAge(int age){
        setAge(age);
        return this;
    }

    public Admin withRole(String role){
        setRole(role);
        return this;
    }

    @Override
    public String toString() {
        return "Admin{" +
                super.toString() +
                ']';
    }

    @Override
    public String getInfo() {
        return "Name: \"" + getName() +
                "\"<br>Age: \"" + getAge() +
                "\"<br>Role: \"" + getRole();
    }
}
