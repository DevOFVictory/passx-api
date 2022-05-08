package net.cuodex.passxapi.controller;

import lombok.Getter;
import net.cuodex.passxapi.dto.AddEntryDto;
import net.cuodex.passxapi.dto.DeleteAccountDto;
import net.cuodex.passxapi.dto.SessionDto;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/entries")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @GetMapping()
    public ResponseEntity<DefaultReturnable> getEntries(@RequestBody SessionDto sessionDto){
        return storageService.getEntries(sessionDto.getSessionId()).getResponseEntity();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultReturnable> getEntryById(@RequestBody SessionDto sessionDto, @PathVariable String id){
        return storageService.getEntryById(sessionDto.getSessionId(), id).getResponseEntity();
    }

    @PostMapping()
    public ResponseEntity<DefaultReturnable> addEntry(@RequestBody AddEntryDto addEntryDto){
        return storageService.addEntry(addEntryDto.getSessionId(), addEntryDto.getEntryService(), addEntryDto.getEntryUrl(), addEntryDto.getEntryDescription(), addEntryDto.getEntryEmail(), addEntryDto.getEntryUsername(), addEntryDto.getEntryPassword()).getResponseEntity();
    }
}
