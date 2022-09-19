package net.cuodex.passxapi.controller;

import net.cuodex.passxapi.dto.AddEntryDto;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.service.AuthenticationService;
import net.cuodex.passxapi.service.RequestService;
import net.cuodex.passxapi.service.StorageService;
import net.cuodex.passxapi.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/entries")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private RequestService requestService;

    @GetMapping("")
    public ResponseEntity<DefaultReturnable> getEntries(@RequestHeader(value = "Authorization") String sessionId, HttpServletRequest request){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return storageService.getEntries(sessionId, requestService.getClientIp(request)).getResponseEntity();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefaultReturnable> getEntryById(@RequestHeader(value = "Authorization") String sessionId, @Valid @PathVariable String id, HttpServletRequest request){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return storageService.getEntryById(sessionId, id, requestService.getClientIp(request)).getResponseEntity();
    }

    @PostMapping("")
    public ResponseEntity<DefaultReturnable> addEntry(@RequestHeader(value = "Authorization") String sessionId, @Valid @RequestBody AddEntryDto addEntryDto, HttpServletRequest request){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return storageService.addEntry(sessionId, addEntryDto.getEntryService(), addEntryDto.getEntryUrl(), addEntryDto.getEntryDescription(), addEntryDto.getEntryEmail(), addEntryDto.getEntryUsername(), addEntryDto.getEntryPassword(), requestService.getClientIp(request)).getResponseEntity();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DefaultReturnable> deleteEntry(@RequestHeader(value = "Authorization") String sessionId, @Valid @PathVariable String id, HttpServletRequest request){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return storageService.deleteEntry(sessionId, id, requestService.getClientIp(request)).getResponseEntity();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DefaultReturnable> updateEntry(@RequestHeader(value = "Authorization") String sessionId, @Valid @RequestBody AddEntryDto ediEntryDto, @Valid @PathVariable String id, HttpServletRequest request){
        sessionId = sessionId.split(" ")[sessionId.split(" ").length - 1];
        return storageService.updateEntry(sessionId, id, ediEntryDto.getEntryService(), ediEntryDto.getEntryUrl(), ediEntryDto.getEntryDescription(), ediEntryDto.getEntryEmail(), ediEntryDto.getEntryUsername(), ediEntryDto.getEntryPassword(), requestService.getClientIp(request)).getResponseEntity();
    }

}
