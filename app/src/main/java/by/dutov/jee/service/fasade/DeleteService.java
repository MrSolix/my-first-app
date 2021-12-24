package by.dutov.jee.service.fasade;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.person.PersonDaoInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteService {
    private final CheckingService checkingService;
    private final PersonDaoInstance personDaoInstance;

    public void deleteUser(HttpServletRequest req, HttpServletResponse resp,
                           String userLogin) throws ServletException, IOException {
        Person person = checkingService.checkUser(userLogin);
        if (person == null || Role.ADMIN.equals(person.getRole())) {
            log.info("User with that userName is not find");
            checkingService.setAttributeAndDispatcher(
                    req, resp,
                    "User with that userName is not find",
                    "errorMessage",
                    "/jsp/admin/deleteUserPage.jsp",
                    DispatcherType.INCLUDE
            );
            return;
        }
        log.info("User is finded");
        req.setAttribute("user", person);
        PersonDAOInterface daoRepository = personDaoInstance.getRepository();
        daoRepository.remove(person);
        checkingService.setAttributeAndDispatcher(
                req, resp,
                "Delete is successful",
                "successMessage",
                "/jsp/admin/deleteUserPage.jsp",
                DispatcherType.INCLUDE
        );
    }

}
