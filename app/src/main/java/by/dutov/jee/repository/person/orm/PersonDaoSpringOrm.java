package by.dutov.jee.repository.person.orm;

import by.dutov.jee.repository.SpringEntityManager;
import by.dutov.jee.repository.group.orm.GroupDaoSpringOrm;
import by.dutov.jee.repository.person.jpa.PersonDaoJpa;
import org.springframework.stereotype.Repository;

@Repository("ormPerson")
public class PersonDaoSpringOrm extends PersonDaoJpa {

    public PersonDaoSpringOrm(SpringEntityManager ormEntityManager, GroupDaoSpringOrm groupDaoSpringOrm) {
        super(ormEntityManager, groupDaoSpringOrm);
    }
}
