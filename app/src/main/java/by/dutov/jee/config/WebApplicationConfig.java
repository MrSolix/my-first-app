package by.dutov.jee.config;

import by.dutov.jee.filters.AdminAccessFilter;
import by.dutov.jee.filters.ContentCachingFilter;
import by.dutov.jee.filters.EncodingFilter;
import by.dutov.jee.filters.LoginFilter;
import by.dutov.jee.filters.StudentAccessFilter;
import by.dutov.jee.filters.TeacherAccessFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;

public class WebApplicationConfig extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {ApplicationConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] {
                new EncodingFilter(),
                new ContentCachingFilter()
        };
    }
}
