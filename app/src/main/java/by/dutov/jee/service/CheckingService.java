package by.dutov.jee.service;

import by.dutov.jee.people.Person;
import by.dutov.jee.repository.RepositoryFactory;
import by.dutov.jee.service.encrypt.PasswordEncryptionService;
import by.dutov.jee.service.exceptions.HashException;
import by.dutov.jee.service.exceptions.PasswordException;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
public class CheckingService {
    private final RepositoryFactory repositoryFactory;

    @Autowired
    public CheckingService(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
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


    public void setAttributeAndDispatcher(HttpServletRequest req, HttpServletResponse resp,
                                          Object object, String attribute,
                                          String path, DispatcherType type
    ) throws ServletException, IOException {
        req.setAttribute(attribute, object);
        CommandServletUtils.dispatcher(req, resp, path, type);
    }

    public Person checkUser(String userName) {
        Optional<? extends Person> person = repositoryFactory.getPersonDaoRepository().find(userName);
        return person.orElse(null);
    }

    public boolean isEmpty(String string) {
        return "".equals(string.trim());
    }


}
