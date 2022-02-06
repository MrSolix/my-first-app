package by.dutov.jee.people;

import by.dutov.jee.auth.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@NamedQuery(name = "findAdminByName", query = "select u from Admin u join u.roles r where u.userName = :name and r.name = 'ADMIN'")
@NamedQuery(name = "findAdminById", query = "select u from Admin u join u.roles r where u.id = :id and r.name = 'ADMIN'")
@NamedQuery(name = "findAllAdmins", query = "select u from Admin u join u.roles r where r.name = 'ADMIN'")
@DiscriminatorValue("admin")
public class Admin extends Person{

    {
        addRole(new Role()
                .withId(3)
                .withName("ADMIN")
                .addPerson(this));
    }

    public Admin withId(Integer id){
        setId(id);
        return this;
    }

    public Admin withUserName(String userName){
        setUserName(userName);
        return this;
    }

    public Admin withPassword(String password){
        setPassword(password);
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

    @Override
    public String infoGet() {
        return "Name: \"" + getName() +
                "\"<br>Age: \"" + getAge() +
                "\"<br>Role: \"" + getRoles() +
                "\"";
    }
}
