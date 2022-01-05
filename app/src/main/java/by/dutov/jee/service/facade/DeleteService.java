package by.dutov.jee.service.facade;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.person.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

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
    private final PersonService personService;

    public ModelAndView deleteUser(ModelAndView modelAndView,
                           String userLogin) {
        InternalResourceView internalResourceView = new InternalResourceView();
        internalResourceView.setAlwaysInclude(true);
        modelAndView.setView(internalResourceView);
        modelAndView.setViewName("/admin/deleteUserPage");
        Person person = checkingService.checkUser(userLogin);
        if (person == null || Role.ADMIN.equals(person.getRole())) {
            log.info("User with that userName is not find");
            modelAndView.addObject("errorMessage",
                    "User with that userName is not find");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
        log.info("User is finded");
        modelAndView.addObject("user", person);
        personService.remove(person);
        modelAndView.addObject("successMessage", "Delete is successful");
        modelAndView.setStatus(HttpStatus.OK);
        return modelAndView;
    }

}
