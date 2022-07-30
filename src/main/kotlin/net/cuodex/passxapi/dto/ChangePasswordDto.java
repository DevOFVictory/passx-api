package net.cuodex.passxapi.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@Data
public class ChangePasswordDto {

    @NotNull
    private String passwordTest;
    @NotNull
    private String newPasswordTest;
    @NotNull
    private List<Map<String, String>> entries;
}
