package net.cuodex.passxapi.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {

    private String sessionId;
    private String passwordTest;
    private String newPasswordTest;
}
