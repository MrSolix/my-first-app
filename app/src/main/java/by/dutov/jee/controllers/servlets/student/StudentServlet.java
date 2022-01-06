package by.dutov.jee.controllers.servlets.student;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServlet;

@Slf4j
@Controller
public class StudentServlet {

    @GetMapping("/student/student")
    public String redirectStudentPage() {
        log.info("Entered Student Page");
        return "/student/studentPage";
    }
}
