package net.cuodex.passxapi.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

public class RequestTooLargeException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    @Setter @Getter
    private String message;

    public RequestTooLargeException(String message) {
        this.setMessage(message);
    }
}
