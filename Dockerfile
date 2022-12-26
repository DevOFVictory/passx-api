FROM openjdk:17
LABEL maintainer="DevOFVictory"
WORKDIR /app
EXPOSE 8443

COPY target/*.jar passx-server.jar
COPY target/keystore.p12 keystore.p12
COPY common-passwords.txt common-passwords.txt

ENTRYPOINT ["java", "-jar", "passx-server.jar", "-Dcom.sun.security.enableAIAcaIssuers=true"]