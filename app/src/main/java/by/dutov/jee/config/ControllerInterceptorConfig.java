package by.dutov.jee.config;

import by.dutov.jee.controllers.servlets.interceptors.LoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class ControllerInterceptorConfig implements WebMvcConfigurer {
    private final LoggingInterceptor loggingInterceptor;
    private final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(openEntityManagerInViewInterceptor());
        registry.addInterceptor(loggingInterceptor);
    }

    @Bean
    public OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor() {
        OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor = new OpenEntityManagerInViewInterceptor();
        openEntityManagerInViewInterceptor.setEntityManagerFactory(entityManagerFactoryBean.getObject());
        return openEntityManagerInViewInterceptor;
    }
}
