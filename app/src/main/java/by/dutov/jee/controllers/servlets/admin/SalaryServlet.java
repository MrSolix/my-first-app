package by.dutov.jee.controllers.servlets.admin;

import by.dutov.jee.service.facade.Finance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping("/admin/salary")
public class SalaryServlet{
    private final Finance finance;

    @Autowired
    public SalaryServlet(Finance finance) {
        this.finance = finance;
    }

    @GetMapping
    public String redirectSalaryPage() {
        return "/admin/salaryPage";
    }

    @PostMapping
    public ModelAndView getSalary(@RequestParam("userName") String userName) {
        ModelAndView modelAndView = new ModelAndView();
        log.info("Get parameters");
        log.info("userName = {}", userName);
        log.info("Get person from db");
        return finance.getSalary(modelAndView, userName);
    }

}
