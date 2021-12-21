package by.dutov.jee.controllers.servlets;

import by.dutov.jee.people.Role;
import by.dutov.jee.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/registration")
public class RegistrationServlet {
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationServlet(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String redirectRegistrationPage() {
        return "/registrationPage";
    }

    @RequestMapping(method = RequestMethod.POST)
    public void averageSalary(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Entered Registration Page");
        log.info("Get parameters");
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        String age = req.getParameter("age");
        Role role = Role.getTypeByStr(req.getParameter("status"));
        log.info("userName = {}, password = ***, name = {}, age = {}, role = {}", userName, name, age, role.getType());
        log.info("Set person from db");
        registrationService.registrationUser(req, resp, userName, password, name, age, role);
    }
}
