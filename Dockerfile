#define base docker image
FROM openjdk:17
LABEL maintainer="DevOFVictory"
ADD target/passx-api-0.0.1-SNAPSHOT.jar passx-server.jar
ADD target/passx-ssl.pkcs12 passx-ssl.pkcs12
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "passx-server.jar"]
