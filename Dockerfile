# Build:  docker build -t jchat .
# Run:    docker run -p 8080:8080 -p 8081:8081 -v $(pwd)/users.json:/app/users.json jchat

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY server/ server/
COPY jar/ jar/

RUN mvn install:install-file \
        -Dfile=jar/jlogm-1.0.jar \
        -DgroupId=com.jlogm \
        -DartifactId=jlogm \
        -Dversion=1.0 \
        -Dpackaging=jar

RUN mvn clean package -pl server -am

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/server/target/server.jar server.jar
RUN echo "[]" > users.json

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "server.jar"]