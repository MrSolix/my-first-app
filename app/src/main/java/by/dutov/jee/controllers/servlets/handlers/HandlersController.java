package by.dutov.jee.controllers.servlets.handlers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/handlers")
public class HandlersController {

    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "/handlers/accessDeniedPage";
    }
}
