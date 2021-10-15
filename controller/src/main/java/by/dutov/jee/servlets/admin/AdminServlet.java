package by.dutov.jee.servlets.admin;

import by.dutov.jee.filters.AdminAccessFilter;
import by.dutov.jee.utils.CommandServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/admin")
public class AdminServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(AdminServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOG.info("Entered admin page.");
        CommandServletUtils.dispatcher(req, resp, "/admin/adminPage.jsp", true);
    }
}
