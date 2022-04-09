package net.cuodex.passxapi;

import net.cuodex.passxapi.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PassxApiApplication {

	public static final Logger LOGGER = LoggerFactory.getLogger(PassxApiApplication.class);

	@Autowired
	private AuthenticationService authenticationManager;

	public static void main(String[] args) {

		SpringApplication.run(PassxApiApplication.class, args);


		LOGGER.info("JSON rest API successfully started.");
	}




}

