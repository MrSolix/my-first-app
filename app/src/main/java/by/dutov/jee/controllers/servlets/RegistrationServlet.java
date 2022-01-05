package by.dutov.jee.controllers.servlets;

import by.dutov.jee.people.Role;
import by.dutov.jee.service.facade.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("/registration")
public class RegistrationServlet {
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationServlet(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public String redirectRegistrationPage() {
        return "/registrationPage";
    }

    @PostMapping
    public ModelAndView registration(@RequestParam("userName") String userName, @RequestParam("password") String password,
                                     @RequestParam("name") String name, @RequestParam("age") String age,
                                     @RequestParam("status") String roleStr) {
        ModelAndView modelAndView = new ModelAndView();
        log.info("Entered Registration Page");
        log.info("Get parameters");
        Role role = Role.getTypeByStr(roleStr);
        log.info("userName = {}, password = ***, name = {}, age = {}, role = {}", userName, name, age, role.getType());
        log.info("Set person from db");
        return registrationService.registrationUser(modelAndView, userName, password, name, age, role);
    }
}
