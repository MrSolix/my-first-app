package by.dutov.jee.service;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.service.exceptions.DataBaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final CheckingService checkingService;
    private final RepositoryFactory repositoryFactory;

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
                    "/jsp/registrationPage.jsp",
                    DispatcherType.INCLUDE);
            return;
        }
        final int age = checkingService.isEmpty(ageStr) ? 0 : Integer.parseInt(ageStr);
        try {
            repositoryFactory.getPersonDaoRepository().save(
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
                    "/jsp/registrationPage.jsp",
                    DispatcherType.INCLUDE
            );
        }
        log.info("registration is successful");
        checkingService.setAttributeAndDispatcher(
                req, resp,
                "Registration is successful",
                "successMessage",
                "/jsp/registrationPage.jsp",
                DispatcherType.INCLUDE
        );
    }
}
