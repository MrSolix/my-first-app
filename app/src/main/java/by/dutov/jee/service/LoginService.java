package by.dutov.jee.service;

import by.dutov.jee.encrypt.PasswordEncryptionService;
import by.dutov.jee.exceptions.HashException;
import by.dutov.jee.exceptions.PasswordException;
import by.dutov.jee.people.Person;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.utils.AppUtils;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

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

    public boolean checkPassword(Person person, String password) {
        final PasswordEncryptionService instance = PasswordEncryptionService.getInstance();
        try {
            return instance.authenticate(password, person.getPassword(), person.getSalt());
        } catch (HashException e) {
            log.error(e.getMessage(), e);
            throw new PasswordException(e);
        }
    }

    public void getLoginedUser(HttpServletRequest req, HttpServletResponse resp, String userName, String password) throws ServletException, IOException {
        Optional<? extends Person> person = RepositoryFactory.getDaoRepository().find(userName);
        boolean checkPass = false;
        if (person.isPresent()) {
            checkPass = LoginService.getInstance().checkPassword(person.get(), password);
        }
        if (!checkPass) {
            log.info("person == null");
            String errorMessage = "Invalid UserName or Password";

            req.setAttribute("errorMessage", errorMessage);
            CommandServletUtils.dispatcher(req, resp, "/loginPage.jsp", false);
        } else if (AppUtils.getLoginedUser(req.getSession()) != null) {
            log.info("already logged in");
            String loginedError = "You need logouted";

            req.setAttribute("loginedError", loginedError);

            CommandServletUtils.dispatcher(req, resp, "/loginPage.jsp", false);
        } else {
            log.info("successful login");
            AppUtils.storeLoginedUser(req.getSession(), person.get());

            CommandServletUtils.dispatcher(req, resp, "/homePage.jsp", true);
        }
    }
}
