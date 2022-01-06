package by.dutov.jee.controllers.servlets.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServlet;

@Slf4j
@Controller
public class AdminServlet {

    @GetMapping("/admin/admin")
    public String redirectAdminPage() {
        log.info("Entered admin page.");
        return "/admin/adminPage";
    }
}
