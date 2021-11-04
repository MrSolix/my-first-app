package by.dutov.jee.people;

import by.dutov.jee.AbstractEntity;
import by.dutov.jee.encrypt.PasswordEncryptionService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class Person extends AbstractEntity implements Printable {
    private String userName;
    private byte[] salt;
    private byte[] password;
    private String name;
    private int age;
    private Role role;

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
}
