FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY commons/pom.xml commons/
COPY server/pom.xml server/
COPY client/pom.xml client/

COPY jar/ jar/
RUN mvn install:install-file \
        -Dfile=jar/jlogm-1.0.jar \
        -DgroupId=com.jlogm \
        -DartifactId=jlogm \
        -Dversion=1.0 \
        -Dpackaging=jar

RUN mvn dependency:go-offline -B

COPY commons/src commons/src
COPY server/src server/src

RUN mvn clean install

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/server/target/server-*.jar server.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "server.jar"]
