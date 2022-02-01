package by.dutov.jee.service.facade;

import by.dutov.jee.auth.Role;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.service.exceptions.DataBaseException;
import by.dutov.jee.service.person.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;


@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final CheckingService checkingService;
    private final PersonService personService;
    private final BCryptPasswordEncoder passwordEncoder;

    public ModelAndView registrationUser(ModelAndView modelAndView,
                                         String userName, String password,
                                         String name, String ageStr, String role){

        InternalResourceView internalResourceView = new InternalResourceView();
        internalResourceView.setAlwaysInclude(true);
        modelAndView.setView(internalResourceView);
        modelAndView.setViewName("registrationPage");
        Person person = checkingService.checkUser(userName);
        if (person != null
                || checkingService.isEmpty(userName, password,
                name, ageStr)) {
            log.info("login is busy or invalid data");
            modelAndView.addObject("errorMessage", "Invalid data");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
        final int age = checkingService.isEmpty(ageStr) ? 0 : Integer.parseInt(ageStr);
        String newPassword = passwordEncoder.encode(password);
        try {
            personService.save(
                    role.equals(Role.ROLE_STUDENT) ?
                            new Student()
                                    .withUserName(userName)
                                    .withPassword(newPassword)
                                    .withName(name)
                                    .withAge(age)
                            :
                            new Teacher()
                                    .withUserName(userName)
                                    .withPassword(newPassword)
                                    .withName(name)
                                    .withAge(age)
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
