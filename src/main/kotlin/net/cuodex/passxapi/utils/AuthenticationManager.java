package net.cuodex.passxapi.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationManager {

    public String authenticate(String username, String password) {

        return UUID.randomUUID().toString();

    }

}
