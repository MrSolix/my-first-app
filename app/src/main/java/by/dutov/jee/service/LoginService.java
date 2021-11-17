package by.dutov.jee.service;

import by.dutov.jee.people.Person;
import by.dutov.jee.utils.AppUtils;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class LoginService {
    private static volatile LoginService instance;

    public LoginService() {
        //singleton
    }

    public static LoginService getInstance() {
        if (instance == null) {
            synchronized (LoginService.class) {
                if (instance == null) {
                    instance = new LoginService();
                }
            }
        }
        return instance;
    }

    public void getLoginedUser(HttpServletRequest req, HttpServletResponse resp, String userName, String password) throws ServletException, IOException {
        CheckingService instance = CheckingService.getInstance();
        Person person = instance.checkUser(userName);
        boolean checkPass = false;
        if (person != null) {
            checkPass = instance.checkPassword(person, password);
        }
        final String errorMessage = "errorMessage";
        final String path = "/loginPage.jsp";
        if (!checkPass) {
            log.info("person == null");
            instance.setAttributeAndDispatcher(req, resp,
                    "Invalid userName or password",
                    errorMessage,
                    path,
                    DispatcherType.INCLUDE
            );
        } else if (AppUtils.getLoginedUser(req.getSession()) != null) {
            log.info("already logged in");
            instance.setAttributeAndDispatcher(req, resp,
                    "You need logouted",
                    errorMessage,
                    path,
                    DispatcherType.INCLUDE
            );
        } else {
            log.info("successful login");
            AppUtils.storeLoginedUser(req.getSession(), person);

            CommandServletUtils.dispatcher(req, resp, "/homePage.jsp", DispatcherType.FORWARD);
        }
    }


}
