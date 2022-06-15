package net.cuodex.passxapi.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
public class UpdateUserInformationDto {

    @NotNull
    private String passwordTest;
    @NotNull
    private Map<String, String> data;

}
