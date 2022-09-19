package net.cuodex.passxapi.interceptor;

import net.cuodex.passxapi.exception.RateLimitedException;
import net.cuodex.passxapi.service.RequestService;
import net.cuodex.passxapi.utils.TimeoutUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class RateLimitInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RequestService requestService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        long timeout = TimeoutUtils.getTimeout(requestService.getClientIp(request), request.getRequestURI());

        if (!Objects.equals(request.getMethod(), HttpMethod.OPTIONS.name())) {
            if (timeout > 0) {
                throw new RateLimitedException("Please wait " + timeout + "ms before you can send this request again.");
            }

            TimeoutUtils.sendRequest(requestService.getClientIp(request), request.getRequestURI());
        }

        return super.preHandle(request, response, handler);
    }
}
