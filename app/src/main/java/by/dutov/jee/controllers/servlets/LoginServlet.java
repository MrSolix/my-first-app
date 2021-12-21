package by.dutov.jee.controllers.servlets;

import by.dutov.jee.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping(value = {"/", "/login"})
public class LoginServlet extends HttpServlet {
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
    public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Entered Login Page");
        log.info("Get parameters");
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        log.info("userName = {}, password = ***", userName);
        log.info("Get person from db");
        loginService.getLoginedUser(req, resp, userName, password);
    }


}
