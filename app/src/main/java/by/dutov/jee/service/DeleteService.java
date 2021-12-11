package by.dutov.jee.service;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.repository.person.PersonDAOInterface;
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
public class DeleteService {
    private final CheckingService checkingService;
    private final RepositoryFactory repositoryFactory;

    @Autowired
    private DeleteService(CheckingService checkingService, RepositoryFactory repositoryFactory) {
        this.checkingService = checkingService;
        this.repositoryFactory = repositoryFactory;
    }

    public void deleteUser(HttpServletRequest req, HttpServletResponse resp,
                           String userLogin) throws ServletException, IOException {
        Person person = checkingService.checkUser(userLogin);
        if (person == null || Role.ADMIN.equals(person.getRole())) {
            log.info("User with that userName is not find");
            checkingService.setAttributeAndDispatcher(
                    req, resp,
                    "User with that userName is not find",
                    "errorMessage",
                    "/admin/deleteUserPage.jsp",
                    DispatcherType.INCLUDE
            );
            return;
        }
        log.info("User is finded");
        req.setAttribute("user", person);
        PersonDAOInterface<Person> daoRepository = repositoryFactory.getPersonDaoRepository();
        daoRepository.remove(person);
        checkingService.setAttributeAndDispatcher(
                req, resp,
                "Delete is successful",
                "successMessage",
                "/admin/deleteUserPage.jsp",
                DispatcherType.INCLUDE
        );
    }

}