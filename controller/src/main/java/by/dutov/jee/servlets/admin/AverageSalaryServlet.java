package by.dutov.jee.servlets.admin;

import by.dutov.jee.Finance;
import by.dutov.jee.dao.PersonRepositoryInMemory;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Teacher;
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
@WebServlet("/admin/average-salary")
public class AverageSalaryServlet extends HttpServlet {

    public static final String MIN_RANGE = "minRange";
    public static final String MAX_RANGE = "maxRange";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Entered Average Salary Page");
        CommandServletUtils.dispatcher(req, resp, "/admin/averageSalaryPage.jsp", true);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Get parameters");
        String userName = req.getParameter("userName");
        int minRange = -1;
        int maxRange = -1;
        if (!req.getParameter(MIN_RANGE).equals("") &&
                !req.getParameter(MAX_RANGE).equals("")) {
            minRange = Integer.parseInt(req.getParameter(MIN_RANGE));
            maxRange = Integer.parseInt(req.getParameter(MAX_RANGE));
        }
        log.info("userName = {}, minRange = {}, maxRange = {}", userName, minRange, maxRange);

        Person person = PersonRepositoryInMemory.getInstance().read(userName);
        log.info("Get person from db");

        if (person == null || !"teacher".equalsIgnoreCase(person.getRole())) {
            log.info("person == null or person role != \"TEACHER\"");
            CommandServletUtils.errorMessage(req, "the teacher's login is incorrect"
                    , "errorStringInAvgSalaryPage");
        } else if (minRange < 1 || maxRange > PersonRepositoryInMemory.CURRENT_MONTH ||
        maxRange <= minRange) {
            log.info("incorrect value in fields \"minRange\" or \"maxRange\"");
            CommandServletUtils.errorMessage(req, "months are incorrect"
                    , "errorMonthsInAvgSalaryPage");
        } else {
            double averageSalary = Finance.averageSalary(minRange, maxRange, (Teacher) person);
            log.info("Average Salary = {}", averageSalary);
            req.setAttribute("averageSalary", averageSalary);
        }
        CommandServletUtils.dispatcher(req, resp, "/admin/averageSalaryPage.jsp", false);
    }
}
