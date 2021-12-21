package by.dutov.jee.controllers.servlets.teacher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class TeacherServlet {

    @GetMapping("/teacher/teacher")
    public String student() {
        log.info("Entered Teacher Page");
        return "/teacher/teacherPage";
    }
}
