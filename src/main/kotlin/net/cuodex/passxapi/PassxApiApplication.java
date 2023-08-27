package net.cuodex.passxapi;

import lombok.Getter;
import net.cuodex.passxapi.utils.AESObject_tmp;
import net.cuodex.passxapi.utils.OtherUtils;
import net.cuodex.passxapi.utils.UmlautHelper;
import net.cuodex.passxapi.utils.Variables;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@SpringBootApplication
public class PassxApiApplication {

	public static final Logger LOGGER = LoggerFactory.getLogger(PassxApiApplication.class);

	@Autowired @Getter
	private Environment env;

	// @Getter
	// private static AESObject_tmp aesObject;

	public static void main(String[] args) {

		SpringApplication.run(PassxApiApplication.class, args);

		LOGGER.info("JSON rest API successfully started.");

		LOGGER.info(Variables.HUTCHA_ENABLED ? "Hutcha enabled." : "Hutcha disabled.");
	}

	@PostConstruct
	private void postConstruct() throws IOException {
		UmlautHelper.init();
		Variables.ENCRYPTION_KEY = env.getProperty("net.cuodex.passx.security.encryptionKey");
		Variables.API_NAME = env.getProperty("net.cuodex.passx.api.name");
		Variables.API_AUTHOR = env.getProperty("net.cuodex.passx.api.author");
		Variables.API_CONTEXT_PATH = env.getProperty("server.servlet.context-path");
		Variables.API_VERSION = env.getProperty("net.cuodex.passx.api.version");
		Variables.API_HOST = env.getProperty("net.cuodex.passx.api.host");

		Variables.MAX_COMMON_PASSWORDS = Integer.parseInt(Objects.requireNonNull(env.getProperty("net.cuodex.passx.security.maxCommonPasswords")));
		Variables.COMMON_PASSWORDS = OtherUtils.readFirst(Path.of("common-passwords.txt"), Variables.MAX_COMMON_PASSWORDS);
		Variables.ENDPOINT_REQUEST_DELAY = Integer.parseInt(Objects.requireNonNull(env.getProperty("net.cuodex.passx.security.endpointPathRequestDelay")));

		Variables.HUTCHA_ENABLED = Boolean.parseBoolean(Objects.requireNonNull(env.getProperty("net.cuodex.passx.security.hutcha.enabled")));
		Variables.HUTCHA_API_HOST = env.getProperty("net.cuodex.passx.security.hutcha.apiHost");
		Variables.SESSION_TIMEOUT = Integer.parseInt(Objects.requireNonNull(env.getProperty("net.cuodex.passx.security.sessionTimeout")));
	}

//	@Bean
//	public ServletWebServerFactory servletContainer() {
//		return new TomcatServletWebServerFactory() {
//			@Override
//			protected void postProcessContext(Context context) {
//				SecurityConstraint securityConstraint = new SecurityConstraint();
//				securityConstraint.setUserConstraint("CONFIDENTIAL");
//				SecurityCollection collection = new SecurityCollection();
//				collection.addPattern("/passx/v3/*");
//				securityConstraint.addCollection(collection);
//				context.addConstraint(securityConstraint);
//			}
//		};
//	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(@NotNull CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH");
			}
		};
	}





}

