package by.dutov.jee.controllers.servlets;

import by.dutov.jee.people.Role;
import by.dutov.jee.service.RegistrationService;
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
@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CommandServletUtils.dispatcher(req, resp, "/registrationPage.jsp", DispatcherType.FORWARD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Entered Registration Page");
        log.info("Get parameters");
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        String age = req.getParameter("age");
        Role role = Role.getTypeByStr(req.getParameter("status"));
        log.info("userName = {}, password = ***, name = {}, age = {}, role = {}", userName, name, age, role.getType());
        log.info("Set person from db");
        RegistrationService.getInstance().registrationUser(
                req, resp,
                userName, password,
                name, age, role
                );
    }
}
