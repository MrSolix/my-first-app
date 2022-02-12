package by.dutov.jee.service.facade;

import by.dutov.jee.people.Person;
import by.dutov.jee.service.person.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckingService {
    private final PersonService personService;

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
