package by.dutov.jee.people;

import by.dutov.jee.AbstractEntity;
import by.dutov.jee.service.encrypt.PasswordEncryptionService;
import by.dutov.jee.service.exceptions.PasswordException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static by.dutov.jee.service.encrypt.PasswordEncryptionService.generateSalt;
import static by.dutov.jee.service.encrypt.PasswordEncryptionService.getEncryptedPassword;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
@MappedSuperclass
public abstract class Person extends AbstractEntity implements Printable {
    @Column(name = "user_name")
    private String userName;
    private byte[] salt;
    private byte[] password;
    @Column(name = "name")
    private String name;
    @Column(name = "age")
    private Integer age;
    @Column(name = "roles", columnDefinition = "enum('STUDENT', 'TEACHER', 'ADMIN')")
    @Enumerated(value = EnumType.STRING)
    private Role role;

    protected void addPassword(String password, Person person){
        byte[] salt;
        byte[] encryptedPassword;
        if (this.getPassword() == null){
            try {
                salt = generateSalt();
                encryptedPassword = getEncryptedPassword(password, salt);
                person.setSalt(salt);
                person.setPassword(encryptedPassword);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                log.error(e.getMessage());
                throw new PasswordException(e);
            }
        }
    }
}
