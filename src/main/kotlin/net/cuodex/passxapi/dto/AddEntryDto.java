package net.cuodex.passxapi.dto;

import lombok.Data;


@Data
public class AddEntryDto {

    private String sessionId;
    private String entryService, entryUrl, entryDescription, entryUsername, EntryEmail, entryPassword;

}
