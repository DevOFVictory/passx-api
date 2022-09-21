package net.cuodex.passxapi.controller;

import com.google.zxing.WriterException;
import net.cuodex.passxapi.PassxApiApplication;
import net.cuodex.passxapi.dto.RegisterDto;
import net.cuodex.passxapi.returnables.DefaultReturnable;
import net.cuodex.passxapi.utils.OtherUtils;
import net.cuodex.passxapi.utils.Variables;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Base64;

@RestController
@CrossOrigin
@RequestMapping("/general")
public class GeneralController {


    @GetMapping("/status")
    public ResponseEntity<DefaultReturnable> getStatus() {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long uptime = rb.getUptime();

        return new DefaultReturnable(HttpStatus.OK, "PassX-API is available :)")
                .addData("status", "running")
                .addData("name", Variables.API_NAME)
                .addData("version", Variables.API_VERSION)
                .addData("developer", "DevOFVictory")
                .addData("host", Variables.API_HOST)
                .addData("contextPath", Variables.API_CONTEXT_PATH)
                .addData("uptime", uptime)
                .addData("sessionTimeout", Variables.SESSION_TIMEOUT)
                .getResponseEntity();
    }

    @GetMapping("/common-passwords")
    public ResponseEntity<DefaultReturnable> getCommonPasswords(@RequestParam("amount") int amount) {

        if (amount > Variables.MAX_COMMON_PASSWORDS)
            return new DefaultReturnable(HttpStatus.BAD_REQUEST, "You can only request the most " + Variables.MAX_COMMON_PASSWORDS + " passwords.").getResponseEntity();

        return new DefaultReturnable(HttpStatus.OK, "Top " + amount + " common passwords received.")
                .addData("passwords", Variables.COMMON_PASSWORDS.subList(0, amount))
                .getResponseEntity();
    }

    @GetMapping(value= "/2fa-code", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] get2faCode(@RequestParam("secret") String secret) throws IOException, WriterException {

        return OtherUtils.getQRCodeImage("otpauth://totp/PassX%20Account?secret="+secret+"&issuer=CuodeX.net&algorithm=SHA512&digits=6&period=30",250,250);

    }

}
