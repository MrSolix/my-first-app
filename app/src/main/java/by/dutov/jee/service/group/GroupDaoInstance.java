package by.dutov.jee.service.group;

import by.dutov.jee.repository.group.GroupDAOInterface;
import by.dutov.jee.service.AbstractDaoInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class GroupDaoInstance extends AbstractDaoInstance {
    private static final String GROUP_DAO_SUFFIX = "Group";
    private GroupDAOInterface repository;
    private final Map<String, GroupDAOInterface> repositoryMap;

    @Autowired
    public GroupDaoInstance(Map<String, GroupDAOInterface> repositoryMap) {
        this.repositoryMap = repositoryMap;
    }

    @PostConstruct
    private void init() {
        repository = repositoryMap.get(repositoryType + GROUP_DAO_SUFFIX);
    }

    public GroupDAOInterface getRepository() {
        return repository;
    }
}
