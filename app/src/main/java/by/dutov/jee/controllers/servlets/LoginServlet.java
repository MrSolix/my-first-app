package by.dutov.jee.controllers.servlets;

import by.dutov.jee.MyAppContext;
import by.dutov.jee.service.CheckingService;
import by.dutov.jee.service.LoginService;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebServlet({"/", "/main/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CommandServletUtils.dispatcher(req, resp, "/loginPage.jsp", DispatcherType.FORWARD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Entered Login Page");
        log.info("Get parameters");
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        log.info("userName = {}, password = ***", userName);
        log.info("Get person from db");
        MyAppContext.getContext().getBean(LoginService.class).getLoginedUser(req, resp, userName, password);
    }


}
