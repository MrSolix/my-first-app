package by.dutov.jee.servlets.teacher;

import by.dutov.jee.utils.CommandServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/teacher/teacher")
public class TeacherServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(TeacherServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Entered Teacher Page");
        CommandServletUtils.dispatcher(req, resp, "/teacher/teacherPage.jsp", true);
    }
}
