package net.cuodex.passxapi.dto;

import lombok.Data;

@Data
public class DeleteAccountDto {

    private String sessionId;
    private String passwordTest;
}
