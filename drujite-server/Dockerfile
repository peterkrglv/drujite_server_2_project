FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY drujite-server-all.jar /app/app.jar

#COPY .env /app/.env
COPY application.yaml /app/application.yaml

##storage for images
RUN mkdir -p /app/storage/images/sessions \
    && mkdir -p /app/storage/images/characters \
    && mkdir -p /app/storage/images/news

VOLUME /app/storage/images/sessions
VOLUME /app/storage/images/characters
VOLUME /app/storage/images/news

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

#
#FROM eclipse-temurin:17-jdk-jammy as builder
#
#WORKDIR /app
#
#COPY build.gradle.kts settings.gradle.kts /app/
#COPY gradle /app/gradle
#COPY src /app/src
#COPY gradlew.bat /app/
#COPY gradle.properties /app/
#COPY gradlew /app/
#
#RUN ./gradlew shadowJar --no-daemon
#
#FROM eclipse-temurin:17-jre-jammy
#
#WORKDIR /app
#
#COPY --from=builder /app/build/libs/*-all.jar /app/app.jar
#
#COPY ./application.yaml /app/application.yaml
#
#RUN mkdir -p /app/storage/images/sessions \
#    && mkdir -p /app/storage/images/characters \
#    && mkdir -p /app/storage/images/news
#
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]