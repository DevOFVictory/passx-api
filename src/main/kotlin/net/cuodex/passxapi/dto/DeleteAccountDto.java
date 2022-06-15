package net.cuodex.passxapi.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class DeleteAccountDto {

    @NotNull
    private String passwordTest;
}
