package net.cuodex.passxapi.dto;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateUserInformationDto {

    private String sessionId;
    private String passwordTest;
    private Map<String, String> data;

}
