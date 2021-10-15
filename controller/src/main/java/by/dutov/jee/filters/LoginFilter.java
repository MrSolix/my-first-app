package by.dutov.jee.filters;

import by.dutov.jee.people.Person;
import by.dutov.jee.utils.AppUtils;
import by.dutov.jee.utils.CommandServletUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        Person loginedUser = AppUtils.getLoginedUser(req.getSession());
        if (loginedUser != null) {
            filterChain.doFilter(req, resp);
        } else {
            CommandServletUtils.dispatcher(req, resp,
                    "/login", true);
        }
    }

    @Override
    public void destroy() {

    }
}