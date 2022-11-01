package net.cuodex.passxapi;

import lombok.Getter;
import net.cuodex.passxapi.service.AuthenticationService;
import net.cuodex.passxapi.utils.AESObject;
import net.cuodex.passxapi.utils.OtherUtils;
import net.cuodex.passxapi.utils.UmlautHelper;
import net.cuodex.passxapi.utils.Variables;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Objects;

@SpringBootApplication
public class PassxApiApplication {

	public static final Logger LOGGER = LoggerFactory.getLogger(PassxApiApplication.class);

	@Autowired @Getter
	private Environment env;

	@Getter
	private static AESObject aesObject;

	@Autowired
	private AuthenticationService authenticationManager;

	public static void main(String[] args) {

		SpringApplication.run(PassxApiApplication.class, args);

		LOGGER.info("JSON rest API successfully started.");
	}

	@PostConstruct
	private void postConstruct() throws IOException {
		UmlautHelper.init();
		aesObject = new AESObject(env.getProperty("net.cuodex.passx.security.encryptionKey"));
		Variables.API_NAME = env.getProperty("net.cuodex.passx.api.name");
		Variables.API_AUTHOR = env.getProperty("net.cuodex.passx.api.author");
		Variables.API_CONTEXT_PATH = env.getProperty("server.servlet.context-path");
		Variables.API_VERSION = env.getProperty("net.cuodex.passx.api.version");
		Variables.API_HOST = env.getProperty("net.cuodex.passx.api.host");

		Variables.MAX_COMMON_PASSWORDS = Integer.parseInt(Objects.requireNonNull(env.getProperty("net.cuodex.passx.security.maxCommonPasswords")));
		Variables.COMMON_PASSWORDS = OtherUtils.readFirst(Path.of("common-passwords.txt"), Variables.MAX_COMMON_PASSWORDS);
		Variables.ENDPOINT_REQUEST_DELAY = Integer.parseInt(Objects.requireNonNull(env.getProperty("net.cuodex.passx.security.endpointPathRequestDelay")));
		Variables.SESSION_TIMEOUT = Integer.parseInt(Objects.requireNonNull(env.getProperty("net.cuodex.passx.security.sessionTimeout")));
	}


	@Bean
	public ServletWebServerFactory servletContainer() {
		// Enable SSL traffic
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/passx/v2/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};

		// Add HTTP to HTTPS redirect
		tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());

		return tomcat;
	}

	/*
    We need to redirect from HTTP to HTTPS. Without SSL, this application used
    port 8082. With SSL it will use port 8443. So, any request for 8082 needs to be
    redirected to HTTPS on 8443.
     */
	private Connector httpToHttpsRedirectConnector() {
		Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		connector.setScheme("http");
		connector.setPort(8080);
		connector.setSecure(false);
		connector.setRedirectPort(8443);
		return connector;
	}

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

