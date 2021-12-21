package by.dutov.jee.controllers.servlets.admin;

import by.dutov.jee.service.UpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/admin/update-user")
public class UpdateUserServlet {
    private final UpdateService updateService;

    @Autowired
    public UpdateUserServlet(UpdateService updateService) {
        this.updateService = updateService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String redirectUpdateUserPage() {
        return "/admin/updateUserPage";
    }

    @RequestMapping(method = RequestMethod.POST)
    public void updateUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Entered Update User Page");
        log.info("Get parameter");
        String userLogin = req.getParameter("userLogin");
        updateService.updateUser(req, resp, userLogin);
    }
}
