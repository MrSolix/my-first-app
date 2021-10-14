package by.dutov.jee.servlets.admin;

import by.dutov.jee.Finance;
import by.dutov.jee.InitializeClass;
import by.dutov.jee.people.Person;
import by.dutov.jee.people.Teacher;
import by.dutov.jee.utils.CommandServletUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/average-salary")
public class AverageSalaryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CommandServletUtils.dispatcher(req, resp, "/admin/averageSalaryPage.jsp", true);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        int minRange = -1;
        int maxRange = -1;
        if (!req.getParameter("minRange").equals("") &&
                !req.getParameter("maxRange").equals("")) {
            minRange = Integer.parseInt(req.getParameter("minRange"));
            maxRange = Integer.parseInt(req.getParameter("maxRange"));
        }

        Person person = InitializeClass.personDAO.read(userName);

        if (person == null || !"teacher".equalsIgnoreCase(person.getRole())) {
            CommandServletUtils.errorMessage(req, "the teacher's login is incorrect"
                    , "errorStringInAvgSalaryPage");
        } else if (minRange < 1 || maxRange > InitializeClass.CURRENT_MONTH ) {
            CommandServletUtils.errorMessage(req, "months are incorrect"
                    , "errorMonthsInAvgSalaryPage");
        } else {
            double averageSalary = Finance.averageSalary(minRange, maxRange, (Teacher) person);
            req.setAttribute("averageSalary", averageSalary);
        }
        CommandServletUtils.dispatcher(req, resp, "/admin/averageSalaryPage.jsp", false);
    }
}
