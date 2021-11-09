package by.dutov.jee.controllers.servlets.admin;

import by.dutov.jee.service.Finance;
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
@WebServlet("/admin/average-salary")
public class AverageSalaryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Entered Average Salary Page");
        CommandServletUtils.dispatcher(req, resp, "/admin/averageSalaryPage.jsp", DispatcherType.FORWARD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Get parameters");
        String userName = req.getParameter("userName");
        Finance.getInstance().getAverageSalary(req, userName);
        CommandServletUtils.dispatcher(req, resp, "/admin/averageSalaryPage.jsp", DispatcherType.INCLUDE);
    }
}
