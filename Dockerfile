FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY demo-1.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]