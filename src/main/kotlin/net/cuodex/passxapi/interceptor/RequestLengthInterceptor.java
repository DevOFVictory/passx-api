package net.cuodex.passxapi.interceptor;

import net.cuodex.passxapi.PassxApiApplication;
import net.cuodex.passxapi.exception.RequestTooLargeException;
import net.cuodex.passxapi.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestLengthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RequestService requestService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getContentLengthLong() >= 2500L) {
            PassxApiApplication.LOGGER.warn(requestService.getClientIp(request) + " sent " + request.getContentLengthLong() + " bytes to " + request.getRequestURI());
            throw new RequestTooLargeException("Your request content length is too large.");
        }

        return super.preHandle(request, response, handler);
    }
}
