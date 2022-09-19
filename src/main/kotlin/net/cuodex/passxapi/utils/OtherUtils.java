package net.cuodex.passxapi.utils;

import net.cuodex.passxapi.PassxApiApplication;
import net.cuodex.passxapi.entity.LoginCredential;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class OtherUtils {

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());
    }

    public static boolean isEmailValid(String email) {
        return email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }

    public static boolean isUsernameValid(String username) {
        return username.matches("^[a-zA-Z0-9_]*$") && username.length() >= 3 && username.length() <= 16;
    }

    public static List<String> readFirst(final Path path, final int numLines) throws IOException, IOException {
        try (final Stream<String> lines = Files.lines(path)) {
            return lines.limit(numLines).collect(toList());
        }
    }

    public static LoginCredential encryptCredential(LoginCredential decryptedCredential) {
        LoginCredential encryptedCredential = new LoginCredential();
        encryptedCredential.setId(decryptedCredential.getId());
        encryptedCredential.setUserAccount(decryptedCredential.getUserAccount());
        encryptedCredential.setTitle(PassxApiApplication.getAesObject().encrypt(decryptedCredential.getTitle()));
        encryptedCredential.setUsername(PassxApiApplication.getAesObject().encrypt(decryptedCredential.getUsername()));
        encryptedCredential.setEmail(PassxApiApplication.getAesObject().encrypt(decryptedCredential.getEmail()));
        encryptedCredential.setPassword(PassxApiApplication.getAesObject().encrypt(decryptedCredential.getPassword()));
        encryptedCredential.setUrl(PassxApiApplication.getAesObject().encrypt(decryptedCredential.getUrl()));
        encryptedCredential.setDescription(PassxApiApplication.getAesObject().encrypt(decryptedCredential.getDescription()));

        return encryptedCredential;
    }

    public static LoginCredential decryptCredential(LoginCredential encryptedCredential) {
        LoginCredential decryptedCredential = new LoginCredential();

        decryptedCredential.setId(encryptedCredential.getId());
        decryptedCredential.setUserAccount(encryptedCredential.getUserAccount());
        decryptedCredential.setTitle(PassxApiApplication.getAesObject().decrypt(encryptedCredential.getTitle()));
        decryptedCredential.setUsername(PassxApiApplication.getAesObject().decrypt(encryptedCredential.getUsername()));
        decryptedCredential.setEmail(PassxApiApplication.getAesObject().decrypt(encryptedCredential.getEmail()));
        decryptedCredential.setPassword(PassxApiApplication.getAesObject().decrypt(encryptedCredential.getPassword()));
        decryptedCredential.setUrl(PassxApiApplication.getAesObject().decrypt(encryptedCredential.getUrl()));
        decryptedCredential.setDescription(PassxApiApplication.getAesObject().decrypt(encryptedCredential.getDescription()));

        return decryptedCredential;
    }

    public static List<String> getSessionIdList(List<PassxUserSession> activeSessions) {
        return activeSessions.stream().map(PassxUserSession::getSessionId).toList();
    }
}
