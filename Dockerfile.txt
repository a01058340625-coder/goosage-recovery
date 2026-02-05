FROM eclipse-temurin:17-jre
WORKDIR /app

COPY target/goosage-api-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]
