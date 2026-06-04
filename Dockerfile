FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y nodejs npm

WORKDIR /app

COPY . .

RUN chmod +x gradlew \
    && ./gradlew build --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/app.jar"]
