#dcoker build stage
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests


#docker run stage

FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/LMS-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]
