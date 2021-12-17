package by.dutov.jee.utils;

import by.dutov.jee.people.Person;
import by.dutov.jee.people.Role;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CommandServletUtils {

    public static void errorMessage(HttpServletRequest req, String error, String nameAttribute) {
        req.setAttribute(nameAttribute, error);
    }

    public static void dispatcher(HttpServletRequest req, HttpServletResponse resp, String path, DispatcherType type) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getServletContext()
                .getRequestDispatcher(path);
        switch (type) {
            case FORWARD:
                dispatcher.forward(req, resp);
                break;
            case INCLUDE:
                dispatcher.include(req, resp);
                break;
        }
    }

    public static void filtredAccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String who) throws ServletException, IOException {
        log.info("Worked {} filter", who);

        Person loginedUser = AppUtils.getLoginedUser(request.getSession());

        if (loginedUser == null || !Role.getStrByType(loginedUser.getRole()).equals(who)) {
            log.info("Access denied");
            CommandServletUtils.dispatcher(request, response,
                    "/main/homePage.jsp", DispatcherType.FORWARD);
        }
        chain.doFilter(request, response);
    }
}
