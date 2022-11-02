package net.cuodex.passxapi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class Confirm2FaCodeDto {
    @NotNull
    private String otp;

    @JsonCreator
    public Confirm2FaCodeDto(@NotNull @JsonProperty("otp") String otp) {
        this.otp = otp;
    }
}
