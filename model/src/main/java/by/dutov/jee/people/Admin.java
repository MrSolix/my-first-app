package by.dutov.jee.people;

public class Admin extends Person{

    public Admin(String login, String password, String name, int age, String role) {
        super(login, password, name, age, role);
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
