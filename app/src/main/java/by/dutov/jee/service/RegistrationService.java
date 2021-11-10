package by.dutov.jee.service;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class RegistrationService {
    private static volatile RegistrationService instance;

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
        Optional<? extends Person> person = RepositoryFactory.getDaoRepository().find(userName);
        if (checkFieldsAndLogined(req, resp, userName, password, name, ageStr, person.isPresent())) {
            return;
        }
        final int age = isEmpty(ageStr) ? 0 : Integer.parseInt(ageStr);
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
        log.info("registration is successful");
        CommandServletUtils.dispatcher(req, resp, "/homePage.jsp", DispatcherType.FORWARD);
    }

    private boolean checkFieldsAndLogined(HttpServletRequest req, HttpServletResponse resp,
                                          String userName, String password,
                                          String name, String ageStr, boolean isPresent)
            throws ServletException, IOException {
        if (isPresent || isaBoolean(userName, password, name, ageStr))
         {
            log.info("login is busy");
            String errorMessage = "Login is busy or invalid data";

            req.setAttribute("errorMessage", errorMessage);
            CommandServletUtils.dispatcher(req, resp, "/registrationPage.jsp", DispatcherType.INCLUDE);
             return true;
        }
        return false;
    }

    private boolean isaBoolean(String userName, String password, String name, String ageStr) {
        return isEmpty(userName)
                || isEmpty(password)
                || isEmpty(name)
                || isEmpty(ageStr);
    }

    private boolean isEmpty(String string) {
        return "".equals(string.trim());
    }
}
