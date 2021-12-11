package by.dutov.jee.service;

import by.dutov.jee.MyAppContext;
import by.dutov.jee.people.Person;
import by.dutov.jee.utils.AppUtils;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Service
public class LoginService {
    private final CheckingService checkingService;

    @Autowired
    private LoginService(CheckingService checkingService) {
        this.checkingService = checkingService;
    }

    public void getLoginedUser(HttpServletRequest req, HttpServletResponse resp, String userName, String password) throws ServletException, IOException {
        Person person = checkingService.checkUser(userName);
        boolean checkPass = false;
        if (person != null) {
            checkPass = checkingService.checkPassword(person, password);
        }
        final String errorMessage = "errorMessage";
        final String path = "/loginPage.jsp";
        if (!checkPass) {
            log.info("person == null");
            checkingService.setAttributeAndDispatcher(req, resp,
                    "Invalid userName or password",
                    errorMessage,
                    path,
                    DispatcherType.INCLUDE
            );
        } else if (AppUtils.getLoginedUser(req.getSession()) != null) {
            log.info("already logged in");
            checkingService.setAttributeAndDispatcher(req, resp,
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
