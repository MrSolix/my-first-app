
package by.dutov.jee.controllers.servlets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeServlet {

    @GetMapping("/main/home")
    public String home() {
        log.info("Entered Home Page");
        return "/main/homePage";
    }
}
