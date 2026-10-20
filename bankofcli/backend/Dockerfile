# ---- build stage ----
    FROM maven:3.9-eclipse-temurin-17 AS build
    WORKDIR /app
    
    COPY pom.xml .
    RUN mvn dependency:go-offline -B
    
    COPY src ./src
    RUN mvn clean package -DskipTests -B
    
    # ---- runtime stage ----
# ---- runtime stage ----
    FROM eclipse-temurin:17-jre
    WORKDIR /app
    
    RUN groupadd -r spring && useradd -r -g spring spring
    USER spring
    
    COPY --from=build /app/target/*.jar app.jar
    
    EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "app.jar"]