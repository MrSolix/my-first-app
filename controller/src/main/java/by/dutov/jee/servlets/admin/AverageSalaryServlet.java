package by.dutov.jee.servlets.admin;

import by.dutov.jee.Finance;
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

@WebServlet("/admin/average-salary")
public class AverageSalaryServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(AverageSalaryServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Entered Average Salary Page");
        CommandServletUtils.dispatcher(req, resp, "/admin/averageSalaryPage.jsp", true);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Get parameters");
        String userName = req.getParameter("userName");
        int minRange = -1;
        int maxRange = -1;
        if (!req.getParameter("minRange").equals("") &&
                !req.getParameter("maxRange").equals("")) {
            minRange = Integer.parseInt(req.getParameter("minRange"));
            maxRange = Integer.parseInt(req.getParameter("maxRange"));
        }
        LOG.info("userName = {}, minRange = {}, maxRange = {}", userName, minRange, maxRange);

        Person person = InitializeClass.personDAO.read(userName);
        LOG.info("Get person from db");

        if (person == null || !"teacher".equalsIgnoreCase(person.getRole())) {
            LOG.info("person == null or person role != \"TEACHER\"");
            CommandServletUtils.errorMessage(req, "the teacher's login is incorrect"
                    , "errorStringInAvgSalaryPage");
        } else if (minRange < 1 || maxRange > InitializeClass.CURRENT_MONTH) {
            LOG.info("incorrect value in fields \"minRange\" or \"maxRange\"");
            CommandServletUtils.errorMessage(req, "months are incorrect"
                    , "errorMonthsInAvgSalaryPage");
        } else {
            double averageSalary = Finance.averageSalary(minRange, maxRange, (Teacher) person);
            LOG.info("Average Salary = {}", averageSalary);
            req.setAttribute("averageSalary", averageSalary);
        }
        CommandServletUtils.dispatcher(req, resp, "/admin/averageSalaryPage.jsp", false);
    }
}
