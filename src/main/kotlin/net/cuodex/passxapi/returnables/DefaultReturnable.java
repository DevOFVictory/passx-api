package net.cuodex.passxapi.returnables;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import net.cuodex.passxapi.utils.OtherUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DefaultReturnable {

    private String status;
    private final String message;
    private String timestamp;
    private Map<String, Object> data;
    @JsonIgnore
    private HttpStatus httpStatus;

    public DefaultReturnable(String message) {
        this.message = message;
        this.timestamp = OtherUtils.getTimestamp();
        this.status = HttpStatus.OK.value() + " - " + HttpStatus.OK.getReasonPhrase();
        this.httpStatus = HttpStatus.OK;
        this.data = new HashMap<>();

    }

    public DefaultReturnable(HttpStatus statusCode, String message) {
        this.message = message;
        this.timestamp = OtherUtils.getTimestamp();
        this.status = statusCode.value() + " - " + statusCode.getReasonPhrase();
        this.httpStatus = statusCode;
        this.data = new HashMap<>();
    }

    public DefaultReturnable addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @JsonIgnore
    public ResponseEntity<DefaultReturnable> getResponseEntity() {
        return ResponseEntity.status(httpStatus).body(this);
    }

}
