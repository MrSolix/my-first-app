package by.dutov.jee.service.facade;

import by.dutov.jee.people.Person;
import by.dutov.jee.service.encrypt.PasswordEncryptionService;
import by.dutov.jee.service.exceptions.HashException;
import by.dutov.jee.service.exceptions.PasswordException;
import by.dutov.jee.service.person.PersonService;
import by.dutov.jee.utils.CommandServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckingService {
    private final PersonService personService;

    public boolean checkPassword(Person person, String password) {
        try {
            return PasswordEncryptionService.authenticate(password, person.getPassword(), person.getSalt());
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
        Optional<? extends Person> person = personService.find(userName);
        return person.orElse(null);
    }

    public boolean isEmpty(String... string) {
        int i = 0;
        for (String s : string) {
            if ("".equals(s.trim())) {
                i++;
            }
        }
        return i > 0;
    }


}
