package by.dutov.jee.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan("by.dutov.jee")
@EnableWebMvc
public class ApplicationConfigTest {

}
