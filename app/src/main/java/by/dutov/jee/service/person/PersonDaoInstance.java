package by.dutov.jee.service.person;

import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.service.AbstractDaoInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class PersonDaoInstance extends AbstractDaoInstance {
    private static final String PERSON_DAO_SUFFIX = "Person";
    private PersonDAOInterface repository;
    private final Map<String, PersonDAOInterface> repositoryMap;

    @Autowired
    public PersonDaoInstance(Map<String, PersonDAOInterface> repositoryMap) {
        this.repositoryMap = repositoryMap;
    }

    @PostConstruct
    private void init() {
        repository = repositoryMap.get(repositoryType + PERSON_DAO_SUFFIX);
    }

    public PersonDAOInterface getRepository() {
        return repository;
    }
}
