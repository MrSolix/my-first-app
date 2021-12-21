
package by.dutov.jee.controllers.servlets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class UserInfoServlet {

    @GetMapping("/main/user-info")
    public String userInfo() {
        log.info("Entered User Info Page");
        return "/main/userInfoPage";
    }
}