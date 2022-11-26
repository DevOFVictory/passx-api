package net.cuodex.passxapi.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class SetSettingDto {

    @NotNull
    private String name;
    @NotNull
    private String value;
}
