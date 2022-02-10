package by.dutov.jee.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CommandServletUtils {

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

}
