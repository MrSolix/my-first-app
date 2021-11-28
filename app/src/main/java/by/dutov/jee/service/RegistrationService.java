package by.dutov.jee.service;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RegistrationService {
    private static volatile RegistrationService instance;
    final CheckingService checkingService = CheckingService.getInstance();

    public RegistrationService() {
        //singleton
    }

    public static RegistrationService getInstance() {
        if (instance == null) {
            synchronized (RegistrationService.class) {
                if (instance == null) {
                    instance = new RegistrationService();
                }
            }
        }
        return instance;
    }

    public void registrationUser(HttpServletRequest req, HttpServletResponse resp,
                                 String userName, String password,
                                 String name, String ageStr, Role role) throws ServletException, IOException {
        Person person = checkingService.checkUser(userName);
        if (person != null
                || checkingService.isEmpty(userName)
                || checkingService.isEmpty(password)
                || checkingService.isEmpty(name)
                || checkingService.isEmpty(ageStr)) {
            log.info("login is busy");
            checkingService.setAttributeAndDispatcher(
                    req, resp,
                    "Login is busy or invalid data",
                    "errorMessage",
                    "/registrationPage.jsp",
                    DispatcherType.INCLUDE);
            return;
        }
        final int age = checkingService.isEmpty(ageStr) ? 0 : Integer.parseInt(ageStr);
        try {
            RepositoryFactory.getDaoRepository().save(
                    role == Role.STUDENT ?
                            new Student()
                                    .withUserName(userName)
                                    .withPassword(password)
                                    .withName(name)
                                    .withAge(age)
                                    .withRole(role)
                            :
                            new Teacher()
                                    .withUserName(userName)
                                    .withPassword(password)
                                    .withName(name)
                                    .withAge(age)
                                    .withRole(role)
            );
        } catch (DataBaseException e) {
            log.info("registration is successful");
            checkingService.setAttributeAndDispatcher(
                    req, resp,
                    "Registration is failed",
                    "errorMessage",
                    "/registrationPage.jsp",
                    DispatcherType.INCLUDE
            );
        }
        log.info("registration is successful");
        checkingService.setAttributeAndDispatcher(
                req, resp,
                "Registration is successful",
                "successMessage",
                "/registrationPage.jsp",
                DispatcherType.INCLUDE
        );
    }
}
