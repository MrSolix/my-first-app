package by.dutov.jee.servlets;

import by.dutov.jee.dao.PersonRepositoryInMemory;
import by.dutov.jee.people.Person;
import by.dutov.jee.utils.AppUtils;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebServlet({"/", "/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CommandServletUtils.dispatcher(req, resp, "/loginPage.jsp", true);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Entered Login Page");
        log.info("Get parameters");
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        log.info("userName = {}, password = ***", userName);
        Person person = PersonRepositoryInMemory.getInstance().read(userName, password);
        log.info("Get person from db");
        if (person == null) {
            log.info("person == null");
            String errorMessage = "Invalid UserName or Password";

            req.setAttribute("errorMessage", errorMessage);
            CommandServletUtils.dispatcher(req, resp, "/loginPage.jsp", false);
        } else if (AppUtils.getLoginedUser(req.getSession()) != null) {
            log.info("already logged in");
            String loginedError = "You need logouted";

            req.setAttribute("loginedError", loginedError);

            CommandServletUtils.dispatcher(req, resp, "/loginPage.jsp", false);
        } else {
            log.info("successful login");
            AppUtils.storeLoginedUser(req.getSession(), person);

            CommandServletUtils.dispatcher(req, resp, "/homePage.jsp", true);
        }
    }
}
