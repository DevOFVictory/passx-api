package net.cuodex.passxapi.dto;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateUserDto {

    private String sessionId;
    private Map<String, String> userData;

}
