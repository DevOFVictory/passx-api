package net.cuodex.passxapi.controller;

import lombok.Getter;
import net.cuodex.passxapi.dto.DeleteAccountDto;
import net.cuodex.passxapi.dto.SessionDto;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/entries")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @GetMapping()
    public ResponseEntity<DefaultReturnable> deleteUser(@RequestBody SessionDto sessionDto){
        return storageService.getEntries(sessionDto.getSessionId()).getResponseEntity();
    }
}
