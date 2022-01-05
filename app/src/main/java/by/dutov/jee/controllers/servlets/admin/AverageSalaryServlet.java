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
@RequestMapping("/admin/average-salary")
public class AverageSalaryServlet {
    private final Finance finance;

    @Autowired
    public AverageSalaryServlet(Finance finance) {
        this.finance = finance;
    }

    @GetMapping
    public String redirectAverageSalaryPage() {
        return "/admin/averageSalaryPage";
    }

    @PostMapping
    public ModelAndView averageSalary(@RequestParam("userName") String userName, @RequestParam("minRange") String min,
            @RequestParam("maxRange") String max) {
        ModelAndView modelAndView = new ModelAndView();
        log.info("Get parameters");
        log.info("userName = {}", userName);
        log.info("Get person from db");
        return finance.getAverageSalary(modelAndView, min, max, userName);
    }
}
