package by.dutov.jee.servlets.student;

import by.dutov.jee.utils.CommandServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/student/student")
public class StudentServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(StudentServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Entered Student Page");
        CommandServletUtils.dispatcher(req, resp, "/student/studentPage.jsp", true);
    }
}
