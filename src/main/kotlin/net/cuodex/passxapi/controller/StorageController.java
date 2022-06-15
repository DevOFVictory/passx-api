package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.dto.AddEntryDto;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/entries")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @GetMapping("")
    public ResponseEntity<DefaultReturnable> getEntries(@RequestHeader(value = "Authorization") String sessionId){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return storageService.getEntries(sessionId).getResponseEntity();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultReturnable> getEntryById(@RequestHeader(value = "Authorization") String sessionId, @Valid @PathVariable String id){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return storageService.getEntryById(sessionId, id).getResponseEntity();
    }

    @PostMapping("")
    public ResponseEntity<DefaultReturnable> addEntry(@RequestHeader(value = "Authorization") String sessionId, @Valid @RequestBody AddEntryDto addEntryDto){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return storageService.addEntry(sessionId, addEntryDto.getEntryService(), addEntryDto.getEntryUrl(), addEntryDto.getEntryDescription(), addEntryDto.getEntryEmail(), addEntryDto.getEntryUsername(), addEntryDto.getEntryPassword()).getResponseEntity();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultReturnable> deleteEntry(@RequestHeader(value = "Authorization") String sessionId, @Valid @PathVariable String id){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return storageService.deleteEntry(sessionId, id).getResponseEntity();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultReturnable> updateEntry(@RequestHeader(value = "Authorization") String sessionId, @Valid @RequestBody AddEntryDto ediEntryDto, @Valid @PathVariable String id){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return storageService.updateEntry(sessionId, id, ediEntryDto.getEntryService(), ediEntryDto.getEntryUrl(), ediEntryDto.getEntryDescription(), ediEntryDto.getEntryEmail(), ediEntryDto.getEntryUsername(), ediEntryDto.getEntryPassword()).getResponseEntity();
    }

}
