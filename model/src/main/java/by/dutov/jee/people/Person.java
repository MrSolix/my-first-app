package by.dutov.jee.people;

import java.util.Objects;

public abstract class Person implements Printable {
    private static long ID = 1;
    private long id;
    private String userName;
    private String password;
    private String name;
    private int age;
    private String role;


    public Person(String userName, String password, String name, int age, String role) {
        this.id = ID++;
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.age = age;
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id &&
                age == person.age &&
                Objects.equals(userName, person.userName) &&
                Objects.equals(password, person.password) &&
                Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, password, name, age);
    }

    @Override
    public String toString() {
        return "<br>person id=" + id +
                "<br>name='" + name + '\'' +
                "<br>age=" + age +
                "<br>role=" + role +
                '}';
    }
}
