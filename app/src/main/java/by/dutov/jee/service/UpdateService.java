package by.dutov.jee.service;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.encrypt.PasswordEncryptionService;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
public class UpdateService {
    private static volatile UpdateService instance;
    final CheckingService checkingService = CheckingService.getInstance();

    public UpdateService() {
        //singleton
    }

    public static UpdateService getInstance() {
        if (instance == null) {
            synchronized (UpdateService.class) {
                if (instance == null) {
                    instance = new UpdateService();
                }
            }
        }
        return instance;
    }

    public void updateUser(HttpServletRequest req, HttpServletResponse resp,
                           String userLogin) throws ServletException, IOException {
        Person person = checkingService.checkUser(userLogin);
        if (person == null) {
            log.info("User with that userName is not find");
            checkingService.setAttributeAndDispatcher(
                    req, resp,
                    "User with that userName is not find",
                    "errorMessage",
                    "/admin/updateUserPage.jsp",
                    DispatcherType.INCLUDE
            );
            return;
        }
        log.info("User is finded");
        boolean isLogin = false;
        boolean isPass = false;
        boolean isName = false;
        boolean isAge = false;
        String[] names = req.getParameterValues("check");
        if (names != null) {
            for (String str : names) {
                if (str.equals("login"))
                    isLogin = true;
                if (str.equals("pass"))
                    isPass = true;
                if (str.equals("name"))
                    isName = true;
                if (str.equals("age"))
                    isAge = true;
            }
        }
        Integer userId = person.getId();
        String userName = person.getUserName();
        byte[] password = person.getPassword();
        byte[] salt = person.getSalt();
        String name = person.getName();
        int age = person.getAge();
        Role role = person.getRole();
        if (!isLogin && !isPass && !isName && !isAge) {
            log.info("Remained unchanged");
            checkingService.setAttributeAndDispatcher(
                    req, resp,
                    "Remained unchanged",
                    "errorMessage",
                    "/admin/updateUserPage.jsp",
                    DispatcherType.INCLUDE
            );
            return;
        }
        if (isLogin) {
            log.info("user name changed");
            userName = req.getParameter("userName");
        }
        if (isPass) {
            log.info("password changed");
            String pass = req.getParameter("password");
            PasswordEncryptionService passwordEncryptionService = PasswordEncryptionService.getInstance();
            try {
                salt = passwordEncryptionService.generateSalt();
                password = passwordEncryptionService.getEncryptedPassword(pass, salt);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                log.error(e.getMessage());
            }
        }
        if (isName) {
            log.info("name changed");
            name = req.getParameter("name");
        }
        if (isAge) {
            log.info("age changed");
            final String ageParam = req.getParameter("age");
            age = checkingService.isEmpty(ageParam) ? 0 : Integer.parseInt(ageParam);
        }
        PersonDAOInterface<Person> daoRepository = RepositoryFactory.getDaoRepository();
        try {
            if (role.equals(Role.STUDENT)) {
                daoRepository.save(
                        new Student()
                                .withId(userId)
                                .withUserName(userName)
                                .withBytePass(password)
                                .withSalt(salt)
                                .withName(name)
                                .withAge(age)
                );
            } else if (role.equals(Role.TEACHER)) {
                daoRepository.save(
                        new Teacher()
                                .withId(userId)
                                .withUserName(userName)
                                .withBytePass(password)
                                .withSalt(salt)
                                .withName(name)
                                .withAge(age)
                );
            }
        } catch (DataBaseException e) {
            log.info("changed failed");
            checkingService.setAttributeAndDispatcher(
                    req, resp,
                    "changed failed",
                    "errorMessage",
                    "/admin/updateUserPage.jsp",
                    DispatcherType.INCLUDE
            );
        }
        log.info("changed successful");
        checkingService.setAttributeAndDispatcher(
                req, resp,
                "changed successful",
                "errorMessage",
                "/admin/updateUserPage.jsp",
                DispatcherType.INCLUDE
        );
    }
}
