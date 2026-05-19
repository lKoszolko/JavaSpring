FROM maven:3.8.5-openjdk-17 AS buiild
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=buiild /app/target/*.jar app.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=jpa"]