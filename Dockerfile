FROM eclipse-temurin:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app_auth.jar
ENTRYPOINT ["java","-jar","/app_auth.jar"]