package by.dutov.jee.servlets;

import by.dutov.jee.utils.CommandServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/user-info")
public class UserInfoServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(UserInfoServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Entered User Info Page");
        CommandServletUtils.dispatcher(req, resp, "/userInfoPage.jsp", true);
    }
}
