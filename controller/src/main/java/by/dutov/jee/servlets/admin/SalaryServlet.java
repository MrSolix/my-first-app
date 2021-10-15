package by.dutov.jee.servlets.admin;

import by.dutov.jee.InitializeClass;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.utils.CommandServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/salary")
public class SalaryServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(SalaryServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Entered Salary Page");
        CommandServletUtils.dispatcher(req, resp, "/admin/salaryPage.jsp", true);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Get parameters");
        String userName = req.getParameter("userName");
        LOG.info("userName = {}", userName);
        Person person = InitializeClass.personDAO.read(userName);
        LOG.info("Get person from db");
        if (person == null || !"teacher".equalsIgnoreCase(person.getRole())) {
            LOG.info("person == null or person role != \"TEACHER\"");
            String errorString = "the teacher's login is incorrect";
            req.setAttribute("errorStringInSalaryPage", errorString);
        } else {
            LOG.info("Salary = {}", ((Teacher) person).getSalary());
            req.setAttribute("teacher", person);
        }
        CommandServletUtils.dispatcher(req, resp, "/admin/salaryPage.jsp", false);
    }

}
