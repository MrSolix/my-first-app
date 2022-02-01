package by.dutov.jee.service.auth;

import by.dutov.jee.auth.UserPrincipal;
import by.dutov.jee.people.Person;
import by.dutov.jee.service.person.PersonDaoInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private PersonDaoInstance personDaoInstance;

    @Autowired
    public void setPersonDaoInstance(PersonDaoInstance personDaoInstance) {
        this.personDaoInstance = personDaoInstance;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> optionalPerson = personDaoInstance.getRepository().find(username);
        Person person = optionalPerson.orElseThrow(() -> {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        });
        return new UserPrincipal(person);
    }
}
