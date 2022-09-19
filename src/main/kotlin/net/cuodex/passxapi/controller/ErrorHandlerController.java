package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.returnables.DefaultReturnable;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
@Order(Ordered.LOWEST_PRECEDENCE)
public class ErrorHandlerController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public ResponseEntity<DefaultReturnable> handleError(HttpServletRequest request) {

        int code = 500;

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            code = Integer.parseInt(status.toString());
        }

        String reason = switch (code) {
            case 400 -> "Your request body contains invalid data that can't be processed.";
            case 403 -> "You don't have permission to use this endpoint.";
            case 404 -> "The endpoint " + request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) + " was not found in this api.";
            case 405 -> "The used " + request.getMethod() + " method is not available for this endpoint.";
            case 408 -> "Due to the duration of processing your request it was canceled.";
            case 411 -> "The Content-Length header is missing.";
            case 500 -> "The server has encountered a situation it does not know how to handle.";
            case 502 -> "This error response means that the server, while working as a gateway to get a response needed to handle the request, got an invalid response.";
            default -> "There was an unknown error, we do not know about much...";
        };

        return new DefaultReturnable(HttpStatus.valueOf(code), reason).getResponseEntity();
    }

}