package by.dutov.jee.servlets;

import by.dutov.jee.people.Admin;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Student;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.utils.AppUtils;
import by.dutov.jee.InitializeClass;
import by.dutov.jee.utils.CommandServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet({"/", "/login"})
public class LoginServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CommandServletUtils.dispatcher(req, resp, "/loginPage.jsp", true);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Entered Login Page");
        LOG.info("Get parameters");
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        LOG.info("userName = {}, password = ***", userName);
        Person person = InitializeClass.personDAO.read(userName, password);
        LOG.info("Get person from db");
        if (person == null) {
            LOG.info("person == null");
            String errorMessage = "Invalid UserName or Password";

            req.setAttribute("errorMessage", errorMessage);
            CommandServletUtils.dispatcher(req, resp, "/loginPage.jsp", false);
        } else if (AppUtils.getLoginedUser(req.getSession()) != null) {
            LOG.info("already logged in");
            String loginedError = "You need logouted";

            req.setAttribute("loginedError", loginedError);

            CommandServletUtils.dispatcher(req, resp, "/loginPage.jsp", false);
        } else {
            LOG.info("successful login");
            AppUtils.storeLoginedUser(req.getSession(), person);

            CommandServletUtils.dispatcher(req, resp, "/homePage.jsp", true);
        }
    }
}
