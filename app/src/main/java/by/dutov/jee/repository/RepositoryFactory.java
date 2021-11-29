package by.dutov.jee.repository;

import by.dutov.jee.people.Person;
import by.dutov.jee.repository.person.PersonDAOInterface;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class RepositoryFactory {
    public static final RepositoryTypes TYPE;
    private static RepositoryDataSource dataSource;
    public static final PersonDAOInterface<Person> dao;

    static {
        Properties appProperties = new Properties();
        try {
            appProperties.load(Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("app.properties"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        TYPE = RepositoryTypes.getTypeByStr(appProperties.getProperty("repository.type"));
        dao = RepositoryTypes.getDaoByType(TYPE);
        if (TYPE == RepositoryTypes.POSTGRES) {
            dataSource = RepositoryDataSource.getInstance(
                    appProperties.getProperty("postgres.driver"),
                    appProperties.getProperty("postgres.uri"),
                    appProperties.getProperty("postgres.user"),
                    appProperties.getProperty("postgres.password"));
        }
    }

    private RepositoryFactory() {
        //factory empty private
    }

    public static PersonDAOInterface<Person> getDaoRepository() {
        return dao;
    }

    public static RepositoryDataSource getDataSource(){
        return dataSource;
    }
}
