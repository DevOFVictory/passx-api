package net.cuodex.passxapi.config;

import net.cuodex.passxapi.interceptor.RateLimitInterceptor;
import net.cuodex.passxapi.interceptor.RequestLengthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RequestConfig implements WebMvcConfigurer {

    @Autowired
    private RequestLengthInterceptor requestLengthInterceptor;

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLengthInterceptor).order(Ordered.HIGHEST_PRECEDENCE);
        registry.addInterceptor(rateLimitInterceptor);
    }
}
