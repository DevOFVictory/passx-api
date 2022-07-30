package net.cuodex.passxapi.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class RegisterDto {

    @NotNull
    private String username;
    @NotNull
    private String email;
    @NotNull
    private String passwordTest;
    @NotNull
    private Boolean serverSideEncryption;
}