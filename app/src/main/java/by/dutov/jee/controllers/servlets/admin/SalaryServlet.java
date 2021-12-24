package by.dutov.jee.controllers.servlets.admin;

import by.dutov.jee.service.fasade.Finance;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/admin/salary")
public class SalaryServlet{
    private final Finance finance;

    @Autowired
    public SalaryServlet(Finance finance) {
        this.finance = finance;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String redirectSalaryPage() {
        return "/admin/salaryPage";
    }

    @RequestMapping(method = RequestMethod.POST)
    public void averageSalary(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Get parameters");
        String userName = req.getParameter("userName");
        log.info("userName = {}", userName);
        log.info("Get person from db");
        finance.getSalary(req, userName);
        CommandServletUtils.dispatcher(req, resp, "/jsp/admin/salaryPage.jsp", DispatcherType.INCLUDE);
    }

}
