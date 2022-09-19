package net.cuodex.passxapi.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

public class RateLimitedException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    private String message;

    public RateLimitedException(String message) {
        this.setMessage(message);
    }
}
