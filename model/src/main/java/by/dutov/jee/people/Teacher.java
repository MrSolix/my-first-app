package by.dutov.jee.people;

import by.dutov.jee.group.Group;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "users")
@Entity
@SecondaryTable(name = "salaries", pkJoinColumns = @PrimaryKeyJoinColumn(name = "teacher_id"))
@NamedQuery(name = "findTeacherByName", query = "from Teacher u where u.userName = :name and u.role = 'TEACHER'")
@NamedQuery(name = "findTeacherById", query = "from Teacher u where u.id = :id and u.role = 'TEACHER'")
@NamedQuery(name = "findAllTeachers", query = "from Teacher u where u.role = 'TEACHER'")
public class Teacher extends Person {
    @ToString.Include
    @EqualsAndHashCode.Include
    @OneToOne(mappedBy = "teacher",
            cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Group group;
    @Column(table = "salaries", name = "salary")
    private Double salary;

    {
        setRole(Role.TEACHER);
    }

    public void setSalary(double salary) {
        if (salary >= 0) {
            this.salary = salary;
        } else {
            this.salary = 0.0;
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

    public void setGroup(Group group) {
        if (group == null) {
            if (this.group != null) {
                this.group.setTeacher(null);
            }
        } else {
            group.setTeacher(this);
        }
        this.group = group;
    }

    public void removeGroup(Group group) {
        this.group = null;
        group.setTeacher(null);
    }

    @Override
    public String infoGet() {
        return "Name: \"" + getName() +
                "\"<br>Age: \"" + getAge() +
                "\"<br>Role: \"" + getRole() +
                "\"<br>Group №: " + (group != null ? group.getId() : 0) +
                "<br>Salary: " + getSalary();
    }
}
