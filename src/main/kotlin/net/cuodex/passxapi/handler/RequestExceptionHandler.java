package net.cuodex.passxapi.handler;

import net.cuodex.passxapi.exception.RateLimitedException;
import net.cuodex.passxapi.exception.RequestTooLargeException;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<DefaultReturnable> handleTooLargeRequestException(RequestTooLargeException exception) {
        return new DefaultReturnable(HttpStatus.BAD_REQUEST, exception.getMessage()).getResponseEntity();
    }

    @ExceptionHandler
    public ResponseEntity<DefaultReturnable> handleRateLimitedException(RateLimitedException exception) {
        return new DefaultReturnable(HttpStatus.TOO_MANY_REQUESTS, exception.getMessage()).getResponseEntity();
    }




}
