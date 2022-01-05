package by.dutov.jee.people;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "users")
@Entity
@NamedQuery(name = "findAdminByName", query = "from Admin u where u.userName = :name and u.role = 'ADMIN'")
@NamedQuery(name = "findAdminById", query = "from Admin u where u.id = :id and u.role = 'ADMIN'")
public class Admin extends Person{

    {
        setRole(Role.ADMIN);
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
        addPassword(password, this);
        return this;
    }

    public Admin withBytePass(byte[] pass){
        setPassword(pass);
        return this;
    }

    public Admin withSalt(byte[] salt){
        setSalt(salt);
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

    public Admin withRole(Role role){
        setRole(role);
        return this;
    }

    @Override
    public String infoGet() {
        return "Name: \"" + getName() +
                "\"<br>Age: \"" + getAge() +
                "\"<br>Role: \"" + getRole() +
                "\"";
    }
}
