package net.cuodex.passxapi.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AddEntryDto {

    private String sessionId;
    private Map<String, String> entryData;
}
