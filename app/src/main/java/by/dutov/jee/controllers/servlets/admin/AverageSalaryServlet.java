package by.dutov.jee.controllers.servlets.admin;

import by.dutov.jee.service.Finance;
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
@RequestMapping("/admin/average-salary")
public class AverageSalaryServlet {
    private final Finance finance;

    @Autowired
    public AverageSalaryServlet(Finance finance) {
        this.finance = finance;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String redirectAverageSalaryPage() {
        return "/admin/averageSalaryPage";
    }

    @RequestMapping(method = RequestMethod.POST)
    public void averageSalary(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Get parameters");
        String userName = req.getParameter("userName");
        log.info("userName = {}", userName);
        log.info("Get person from db");
        finance.getAverageSalary(req, userName);
        CommandServletUtils.dispatcher(req, resp, "/jsp/admin/averageSalaryPage.jsp", DispatcherType.INCLUDE);
    }
}
