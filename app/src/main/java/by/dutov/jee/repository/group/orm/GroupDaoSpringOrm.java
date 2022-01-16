package by.dutov.jee.repository.group.orm;

import by.dutov.jee.repository.SpringEntityManager;
import by.dutov.jee.repository.group.jpa.GroupDaoJpa;
import org.springframework.stereotype.Repository;

@Repository("ormGroup")
public class GroupDaoSpringOrm extends GroupDaoJpa {

    public GroupDaoSpringOrm(SpringEntityManager ormEntityManager) {
        super(ormEntityManager);
    }
}
