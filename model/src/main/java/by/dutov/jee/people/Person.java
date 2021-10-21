package by.dutov.jee.people;

import by.dutov.jee.encrypt.PasswordEncryptionService;
import lombok.Data;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Data
public abstract class Person implements Printable {
    private static long ID = 1;
    private long id;
    private String userName;
    private byte[] salt;
    private byte[] password;
    private String name;
    private int age;
    private String role;

    public Person() {
        id = ID++;
    }

    protected void addPassword(String password, Person person){
        PasswordEncryptionService instance = PasswordEncryptionService.getInstance();
        byte[] salt;
        byte[] encryptedPassword;
        if (this.getPassword() == null){
            try {
                salt = instance.generateSalt();
                encryptedPassword = instance.getEncryptedPassword(password, salt);
                person.setSalt(salt);
                person.setPassword(encryptedPassword);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
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
