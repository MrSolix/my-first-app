package by.dutov.jee.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:app.properties")
public abstract class AbstractDaoInstance {
    @Value("${repository.type}")
    protected String repositoryType;
}
