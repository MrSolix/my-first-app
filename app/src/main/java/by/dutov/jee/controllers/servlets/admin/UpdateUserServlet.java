package by.dutov.jee.controllers.servlets.admin;

import by.dutov.jee.service.facade.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/admin/update-user")
public class UpdateUserServlet {
    private final UpdateService updateService;

    @Autowired
    public UpdateUserServlet(UpdateService updateService) {
        this.updateService = updateService;
    }

    @GetMapping
    public String redirectUpdateUserPage() {
        return "/admin/updateUserPage";
    }

    @PostMapping
    public ModelAndView updateUser(@RequestParam("userLogin") String userLogin, HttpServletRequest req) {
        ModelAndView modelAndView = new ModelAndView();
        log.info("Entered Update User Page");
        log.info("Get parameter");
        return updateService.updateUser(modelAndView, userLogin, req);
    }
}
