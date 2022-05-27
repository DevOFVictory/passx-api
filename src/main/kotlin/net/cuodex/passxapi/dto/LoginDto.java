package net.cuodex.passxapi.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;


@Data
public class LoginDto {

    @NotNull
    private String username;
    @NotNull
    private String passwordTest;
}