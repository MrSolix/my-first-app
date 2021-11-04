package by.dutov.jee.filters;

import by.dutov.jee.utils.CommandServletUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/teacher/*")
public class TeacherAccessFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CommandServletUtils.filtredAccess((HttpServletRequest) request,
                (HttpServletResponse) response, chain, "teacher");
    }
}
