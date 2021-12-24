package by.dutov.jee.controllers.servlets;

import by.dutov.jee.service.fasade.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping(value = {"/", "/login"})
public class LoginServlet {
    private final LoginService loginService;

    @Autowired
    public LoginServlet(LoginService loginService) {
        this.loginService = loginService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String redirectLoginPage() {
        return "/loginPage";
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView login(@RequestParam("userName") String userName, @RequestParam("password") String pass,
                              HttpSession httpSession) {
        ModelAndView modelAndView = new ModelAndView();
        log.info("Entered Login Page");
        log.info("Get parameters");
        log.info("userName = {}, password = ***", userName);
        log.info("Get person from db");
        loginService.getLoginedUser(httpSession, modelAndView, userName, pass);
        return modelAndView;
    }


}
