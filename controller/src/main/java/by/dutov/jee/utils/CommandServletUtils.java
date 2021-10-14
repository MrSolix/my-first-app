package by.dutov.jee.utils;

import by.dutov.jee.people.Person;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CommandServletUtils {
    public static void errorMessage(HttpServletRequest req, String error, String nameAttribute){
        req.setAttribute(nameAttribute, error);
    }
    public static void dispatcher(HttpServletRequest req, HttpServletResponse resp, String path, boolean type) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getServletContext()
                .getRequestDispatcher(path);
        if (type){
            dispatcher.forward(req, resp);
        } else {
            dispatcher.include(req, resp);
        }
    }

    public static void filtredAccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String who) throws ServletException, IOException {
        HttpServletRequest req = request;
        HttpServletResponse resp = response;

        Person loginedUser = AppUtils.getLoginedUser(req.getSession());

        if (loginedUser == null || !who.equalsIgnoreCase(loginedUser.getRole())) {
            CommandServletUtils.dispatcher(req, resp,
                    "/home", true);
        }
        chain.doFilter(req, resp);
    }
}
