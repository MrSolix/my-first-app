package by.dutov.jee.filters;

import by.dutov.jee.controllers.servlets.interceptors.MyContentCachingRequestWrapper;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContentCachingFilter  extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MyContentCachingRequestWrapper reqWrapper = new MyContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper respWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
        chain.doFilter(reqWrapper, respWrapper);
        respWrapper.copyBodyToResponse();
    }
}
