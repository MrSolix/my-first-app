package by.dutov.jee.repository;

import by.dutov.jee.MyAppContext;
import by.dutov.jee.group.Group;
import by.dutov.jee.people.Person;
import by.dutov.jee.repository.group.GroupDAOInterface;
import by.dutov.jee.repository.person.PersonDAOInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@PropertySource("classpath:app.properties")
public class RepositoryFactory {
    public static final String PERSON = "Person";
    public static final String GROUP = "Group";
    @Value("${repository.type}")
    private String stringType;

    public PersonDAOInterface<Person> getPersonDaoRepository() {
        return (PersonDAOInterface<Person>) MyAppContext.getContext().getBean(stringType + PERSON);
    }

    public GroupDAOInterface<Group> getGroupDaoRepository() {
        return (GroupDAOInterface<Group>) MyAppContext.getContext().getBean(stringType + GROUP);
    }
}
