package by.dutov.jee.controllers.servlets.admin;

import by.dutov.jee.service.facade.DeleteService;
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
@RequestMapping("/admin/delete-user")
public class DeleteUserServlet {
    private final DeleteService deleteService;

    @Autowired
    public DeleteUserServlet(DeleteService deleteService) {
        this.deleteService = deleteService;
    }

    @GetMapping
    public String redirectDeleteUserPage() {
        return "/admin/deleteUserPage";
    }

    @PostMapping
    public ModelAndView deleteUser(@RequestParam("userName") String userName) {
        ModelAndView modelAndView = new ModelAndView();
        log.info("Get parameters");
        log.info("userName = {}", userName);
        log.info("Get person from db");
        return deleteService.deleteUser(modelAndView, userName);
    }
}
