package by.dutov.jee.service.facade;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.service.exceptions.DataBaseException;
import by.dutov.jee.service.person.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;


@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final CheckingService checkingService;
    private final PersonService personService;

    public ModelAndView registrationUser(ModelAndView modelAndView,
                                 String userName, String password,
                                 String name, String ageStr, Role role){
        InternalResourceView internalResourceView = new InternalResourceView();
        internalResourceView.setAlwaysInclude(true);
        modelAndView.setView(internalResourceView);
        modelAndView.setViewName("registrationPage");
        Person person = checkingService.checkUser(userName);
        if (person != null
                || checkingService.isEmpty(userName, password,
                name, ageStr)) {
            log.info("login is busy or invalid data");
            modelAndView.addObject("errorMessage", "Login is busy or invalid data");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
        final int age = checkingService.isEmpty(ageStr) ? 0 : Integer.parseInt(ageStr);
        try {
            personService.save(
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
            log.info("registration is failed");
            modelAndView.addObject("errorMessage", "Registration is failed");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
        log.info("registration is successful");
        modelAndView.addObject("successMessage", "Registration is successful");
        modelAndView.setStatus(HttpStatus.OK);
        return modelAndView;
    }
}
