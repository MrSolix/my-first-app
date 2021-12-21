package by.dutov.jee.controllers.servlets.admin;

import by.dutov.jee.service.DeleteService;
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
@RequestMapping("/admin/delete-user")
public class DeleteUserServlet {
    private final DeleteService deleteService;

    @Autowired
    public DeleteUserServlet(DeleteService deleteService) {
        this.deleteService = deleteService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String redirectDeleteUserPage() {
        return "/admin/deleteUserPage";
    }

    @RequestMapping(method = RequestMethod.POST)
    public void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Get parameters");
        String userName = req.getParameter("userName");
        log.info("userName = {}", userName);
        log.info("Get person from db");
        deleteService.deleteUser(req, resp, userName);
    }
}
