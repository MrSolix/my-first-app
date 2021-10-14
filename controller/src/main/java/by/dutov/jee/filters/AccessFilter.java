package by.dutov.jee.filters;

import by.dutov.jee.people.Person;
import by.dutov.jee.utils.AppUtils;
import by.dutov.jee.utils.CommandServletUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter({"/admin/*", "/teacher/*", "/student/*"})
public class AccessFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        Person loginedUser = AppUtils.getLoginedUser(req.getSession());

        if (loginedUser == null) {
            CommandServletUtils.dispatcher(req, resp,
                    "/login", true);
        } else if ("student".equalsIgnoreCase(loginedUser.getRole())) {
            CommandServletUtils.dispatcher(req, resp,
                    "/student/student", true);
        } else if ("teacher".equalsIgnoreCase(loginedUser.getRole())) {
            CommandServletUtils.dispatcher(req, resp,
                    "/teacher/teacher", true);
        } else if ("admin".equalsIgnoreCase(loginedUser.getRole())) {
            CommandServletUtils.dispatcher(req, resp,
                    "/admin/admin", true);
        }
        chain.doFilter(req, resp);
    }
}
