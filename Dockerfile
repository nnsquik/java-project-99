FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x gradlew \
    && mkdir -p src/main/resources/certs \
    && openssl genrsa -out src/main/resources/certs/private.pem 2048 \
    && openssl rsa -in src/main/resources/certs/private.pem \
       -pubout -out src/main/resources/certs/public.pem \
    && ./gradlew build --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/app.jar"]