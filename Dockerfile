FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copia el JAR de la etapa 'build' a la etapa actual
COPY --from=build /app/target/lab2arq.jar lab2arq.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","lab2arq.jar"]