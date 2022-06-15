package net.cuodex.passxapi.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class ChangePasswordDto {

    @NotNull
    private String passwordTest;
    @NotNull
    private String newPasswordTest;
}
