package net.cuodex.passxapi.returnables;

import lombok.Getter;
import lombok.Setter;
import net.cuodex.passxapi.utils.OtherUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ResponseStatus(value = HttpStatus.ACCEPTED)
public class DefaultReturnable {

    // 404 - NOT FOUND

    private String status;
    private final String message;
    private String timestamp;
    private Map<String, Object> data;

    public DefaultReturnable(String message) {
        this.message = message;
        this.timestamp = OtherUtils.getTimestamp();
        this.status = HttpStatus.OK.value() + " - " + HttpStatus.OK.getReasonPhrase();
        this.data = new HashMap<>();
    }

    public DefaultReturnable(HttpStatus statusCode, String message) {
        this.message = message;
        this.timestamp = OtherUtils.getTimestamp();
        this.status = statusCode.value() + " - " + statusCode.getReasonPhrase();
        this.data = new HashMap<>();
    }

    public DefaultReturnable addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

}
