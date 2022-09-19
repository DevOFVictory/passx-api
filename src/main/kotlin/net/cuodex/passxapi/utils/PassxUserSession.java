package net.cuodex.passxapi.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.UUID;

@Data
public class PassxUserSession {

    private Long accountId;
    private String sessionId;
    @JsonIgnore
    private String ipAddress;
    private Long createdAt;
    private boolean activated;

    public PassxUserSession(long accountId, String ipAddress) {
        sessionId = UUID.randomUUID().toString();
        activated = true;
        createdAt = System.currentTimeMillis();
        this.accountId = accountId;
        this.ipAddress = ipAddress;
    }
}
