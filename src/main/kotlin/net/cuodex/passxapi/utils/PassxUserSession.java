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
    private Long lastUsed;
    private boolean activated;

    public PassxUserSession(long accountId, String ipAddress) {
        sessionId = UUID.randomUUID().toString();
        activated = true;
        long time = System.currentTimeMillis();
        createdAt = time;
        lastUsed = time;
        this.accountId = accountId;
        this.ipAddress = ipAddress;
    }
}
