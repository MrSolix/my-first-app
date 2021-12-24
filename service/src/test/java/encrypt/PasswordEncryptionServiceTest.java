package encrypt;

import by.dutov.jee.service.encrypt.PasswordEncryptionService;
import by.dutov.jee.service.exceptions.HashException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static by.dutov.jee.service.encrypt.PasswordEncryptionService.generateSalt;
import static by.dutov.jee.service.encrypt.PasswordEncryptionService.getEncryptedPassword;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Slf4j
public class PasswordEncryptionServiceTest {

    @Test
    public void authenticate() {
        //creating test data
        String somePass = "Password";
        byte[] salt = null;
        byte[] encryptedPassword = null;
        try {
            salt = generateSalt();
            encryptedPassword = getEncryptedPassword(somePass, salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Test password creation error", e);
        }

        //condition
        boolean condition = false;
            try {
                condition = PasswordEncryptionService.authenticate(somePass, encryptedPassword, salt);
            } catch (HashException e) {
                e.printStackTrace();
                log.error("Test password verification error");
            }


        //assert
        assertTrue(condition);
    }

    @Test
    public void generateSalt_NO_NULL() {
        //expected
        byte[] expected = null;
        try {
            expected = generateSalt();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //assert
        assertNotNull(expected);
    }
}