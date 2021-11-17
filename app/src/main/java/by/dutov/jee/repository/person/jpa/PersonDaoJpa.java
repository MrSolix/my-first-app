package by.dutov.jee.repository.person.jpa;

import by.dutov.jee.people.Person;
import by.dutov.jee.repository.person.postgres.PersonDAOPostgres;

import javax.sql.DataSource;

public class PersonDaoJpa extends AbstractPersonDaoJpa<Person> {
    private static volatile PersonDaoJpa instance;
    private final DataSource dataSource;

    public PersonDaoJpa(DataSource dataSource) {
        this.dataSource = dataSource;
        //singleton
    }

    public static PersonDaoJpa getInstance(DataSource dataSource) {
        if (instance == null) {
            synchronized (PersonDaoJpa.class) {
                if (instance == null) {
                    instance = new PersonDaoJpa(dataSource);
                }
            }
        }
        return instance;
    }
}
