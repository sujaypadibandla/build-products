FROM eclipse-temurin:21-jdk-alpine AS build
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
ENV JAVA_OPTS="-Xmx200m"
COPY --from=build /app/target/*.jar app.jar
WORKDIR /app
FROM eclipse-temurin:21-jre-alpine

RUN ./mvnw package -DskipTests -B
COPY src src
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY .mvn .mvn
COPY mvnw pom.xml ./
WORKDIR /app

