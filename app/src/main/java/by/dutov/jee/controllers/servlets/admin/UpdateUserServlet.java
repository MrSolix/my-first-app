package by.dutov.jee.controllers.servlets.admin;

import by.dutov.jee.MyAppContext;
import by.dutov.jee.service.CheckingService;
import by.dutov.jee.service.UpdateService;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebServlet("/admin/update-user")
public class UpdateUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CommandServletUtils.dispatcher(req, resp, "/admin/updateUserPage.jsp", DispatcherType.FORWARD);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UpdateService updateService = MyAppContext.getContext().getBean(UpdateService.class);
        log.info("Entered Update User Page");
        log.info("Get parameter");
        String userLogin = req.getParameter("userLogin");
        updateService.updateUser(req, resp, userLogin);
    }
}
