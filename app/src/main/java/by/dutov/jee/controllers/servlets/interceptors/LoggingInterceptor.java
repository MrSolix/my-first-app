package by.dutov.jee.controllers.servlets.interceptors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Slf4j
@Component
public class LoggingInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        logUrl(req);
        logHeaders(req);
        logBody(req);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object handler, ModelAndView modelAndView) throws Exception {
        ContentCachingResponseWrapper respWrapper = (ContentCachingResponseWrapper) resp;
        String body = new String(respWrapper.getContentAsByteArray(), respWrapper.getCharacterEncoding());
        log.info("Response body:\n{}", body);
    }

    @SneakyThrows
    private void logBody(HttpServletRequest req) {
        MyContentCachingRequestWrapper reqWrapper = (MyContentCachingRequestWrapper) req;
        String body = new String(reqWrapper.getContentAsByteArray(), reqWrapper.getCharacterEncoding());
        log.info("Request body:\n{}", body);
    }

    private void logHeaders(HttpServletRequest req) {
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            log.info("[{}]: {}", name, req.getHeader(name));
        }
    }

    private void logUrl(HttpServletRequest req) {
        log.info("{} {}", req.getMethod(), ServletUriComponentsBuilder.fromRequest(req).toUriString());
    }
}
