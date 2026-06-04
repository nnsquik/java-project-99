FROM eclipse-temurin:21-jdk

RUN apt-get update && apt-get install -y nodejs npm openssl

WORKDIR /app

COPY . .

RUN mkdir -p certs \
    && openssl genrsa -out certs/private.pem 2048 \
    && openssl rsa -in certs/private.pem -pubout -out certs/public.pem \
    && chmod +x gradlew \
    && ./gradlew build --no-daemon

EXPOSE 8080

CMD ["java", "-jar", "build/libs/app.jar"]
