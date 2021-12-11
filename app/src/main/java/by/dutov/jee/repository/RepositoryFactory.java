package by.dutov.jee.repository;

import by.dutov.jee.MyAppContext;
import by.dutov.jee.group.Group;
import by.dutov.jee.people.Person;
import by.dutov.jee.repository.group.GroupDAOInterface;
import by.dutov.jee.repository.group.jpa.GroupDaoJpa;
import by.dutov.jee.repository.group.postgres.GroupDAOPostgres;
import by.dutov.jee.repository.person.PersonDAOInterface;
import by.dutov.jee.repository.person.jpa.PersonDaoJpa;
import by.dutov.jee.repository.person.memory.PersonDAOInMemory;
import by.dutov.jee.repository.person.postgres.PersonDAOPostgres;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@PropertySource("classpath:app.properties")
public class RepositoryFactory {
    @Value("${repository.type}")
    private String stringType;
    private RepositoryTypes type;

    @PostConstruct
    private void init() {
        type = RepositoryTypes.getTypeByStr(stringType);
    }

    public PersonDAOInterface<Person> getPersonDaoRepository() {
        switch (type) {
            case MEMORY:
                return MyAppContext.getContext().getBean(PersonDAOInMemory.class);
            case POSTGRES:
                return MyAppContext.getContext().getBean(PersonDAOPostgres.class);
            case JPA:
            default:
                return MyAppContext.getContext().getBean(PersonDaoJpa.class);
        }
    }

    public GroupDAOInterface<Group> getGroupDaoRepository() {
        switch (type) {
            case MEMORY:
            case POSTGRES:
                return MyAppContext.getContext().getBean(GroupDAOPostgres.class);
            case JPA:
            default:
                return MyAppContext.getContext().getBean(GroupDaoJpa.class);
        }
    }
}
