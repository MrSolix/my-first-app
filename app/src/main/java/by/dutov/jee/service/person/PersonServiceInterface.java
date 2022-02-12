package by.dutov.jee.service.person;

import by.dutov.jee.people.Person;
import by.dutov.jee.repository.DAOInterface;

import java.util.Optional;

public interface PersonServiceInterface extends DAOInterface<Person> {

    Optional<Person> find(String name);

}
