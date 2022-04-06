package net.cuodex.passxapi.returnables;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DefaultReturnable {

    private int status;
    private final String message;
    private String timestamp;
    private Map<String, Object> data;

    public DefaultReturnable(String message) {
        this.message = message;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());
        this.status = HttpStatus.OK.value();
        this.data = new HashMap<>();
    }

    public DefaultReturnable(int statusCode, String message) {
        this.message = message;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());
        this.status = statusCode;
        this.data = new HashMap<>();
    }

    public DefaultReturnable addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

}
