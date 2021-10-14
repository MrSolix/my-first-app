package by.dutov.jee.utils;

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
}
