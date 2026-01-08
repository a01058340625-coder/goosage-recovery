FROM eclipse-temurin:17-jre
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV SPRING_PROFILES_ACTIVE=edge
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]
