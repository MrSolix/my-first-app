package by.dutov.jee.service.encrypt;

import by.dutov.jee.service.exceptions.HashException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class PasswordEncryptionService {

    private static volatile PasswordEncryptionService instance;

    public PasswordEncryptionService() {
    }

    public static PasswordEncryptionService getInstance(){
        if (instance == null){
            synchronized (PasswordEncryptionService.class){
                if (instance == null){
                    instance = new PasswordEncryptionService();
                }
            }
        }
        return instance;
    }

    public boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt) throws HashException {
        byte[] encryptedAttemptedPassword;
        try {
            encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);
            return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new HashException("Ошибка с хэшированием пароля", e);
        }
    }

    public byte[] getEncryptedPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algorithm = "PBKDF2WithHmacSHA1";
        int derivedKeyLength = 160;
        int iterations = 20000;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);

        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

        return f.generateSecret(spec).getEncoded();
    }

    public byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        byte[] salt = new byte[8];
        random.nextBytes(salt);

        return salt;
    }
}
