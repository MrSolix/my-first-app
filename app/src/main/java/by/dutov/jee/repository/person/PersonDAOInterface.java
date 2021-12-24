package by.dutov.jee.repository.person;

import by.dutov.jee.people.Person;
import by.dutov.jee.repository.DAOInterface;

import java.util.List;
import java.util.Optional;

public interface PersonDAOInterface extends DAOInterface<Person> {

    Optional<Person> find(String name);

}
