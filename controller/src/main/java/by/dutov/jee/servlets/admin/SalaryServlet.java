package by.dutov.jee.servlets.admin;

import by.dutov.jee.InitializeClass;
import by.dutov.jee.people.Person;
import by.dutov.jee.utils.CommandServletUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/salary")
public class SalaryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CommandServletUtils.dispatcher(req, resp, "/admin/salaryPage.jsp", false);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        Person person = InitializeClass.personDAO.read(userName);
        if (person == null || !"teacher".equalsIgnoreCase(person.getRole())){
            String errorString = "the teacher's login is incorrect";

            req.setAttribute("errorStringInSalaryPage", errorString);

        } else {
            req.setAttribute("teacher", person);

        }
        CommandServletUtils.dispatcher(req, resp, "/admin/salaryPage.jsp", false);
    }

}
