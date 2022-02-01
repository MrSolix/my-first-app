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
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateService {
    private final CheckingService checkingService;
    private final PersonService personService;
    private final BCryptPasswordEncoder passwordEncoder;

    public ModelAndView updateUser(ModelAndView modelAndView,
                                   String userLogin, HttpServletRequest req) {
        InternalResourceView view = new InternalResourceView();
        view.setAlwaysInclude(true);
        modelAndView.setView(view);
        modelAndView.setViewName("/admin/updateUserPage");
        Person person = checkingService.checkUser(userLogin);
        if (person == null) {
            log.info("User with that userName is not find");
            modelAndView.addObject("errorMessage",
                    "User with that userName is not find");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
        log.info("User found");
        boolean isLogin = false;
        boolean isPass = false;
        boolean isName = false;
        boolean isAge = false;
        String[] names = req.getParameterValues("check");
        if (names != null) {
            for (String str : names) {
                if ("login".equals(str))
                    isLogin = true;
                if ("pass".equals(str))
                    isPass = true;
                if ("name".equals(str))
                    isName = true;
                if ("age".equals(str))
                    isAge = true;
            }
        }
        Integer userId = person.getId();
        String userName = person.getUserName();
        String password = person.getPassword();
        String name = person.getName();
        int age = person.getAge();
        Collection<Role> roles = person.getRoles();
        if (!isLogin && !isPass && !isName && !isAge) {
            log.info("Remained unchanged");
            modelAndView.addObject("errorMessage", "Remained unchanged");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
        if (isLogin) {
            log.info("user name changed");
            userName = req.getParameter("userName");
        }
        if (isPass) {
            log.info("password changed");
            password = passwordEncoder.encode(req.getParameter("password"));
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
        try {
            if (person.getRolesName(roles).contains(Role.ROLE_STUDENT)) {
                personService.save(
                        new Student()
                                .withId(userId)
                                .withUserName(userName)
                                .withPassword(password)
                                .withName(name)
                                .withAge(age)
                );
            } else if (person.getRolesName(roles).contains(Role.ROLE_TEACHER)) {
                personService.save(
                        new Teacher()
                                .withId(userId)
                                .withUserName(userName)
                                .withPassword(password)
                                .withName(name)
                                .withAge(age)
                );
            }
        } catch (DataBaseException e) {
            log.info("changed failed");
            modelAndView.addObject("errorMessage", "changed failed");
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
        log.info("changed successful");
        modelAndView.addObject("errorMessage", "changed successful");
        modelAndView.setStatus(HttpStatus.OK);
        return modelAndView;
    }
}
