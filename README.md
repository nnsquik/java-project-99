[![Actions Status](https://github.com/nnsquik/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/nnsquik/java-project-99/actions)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=nnsquik_java-project-99&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=nnsquik_java-project-99)
[![CI](https://github.com/nnsquik/java-project-99/actions/workflows/ci.yml/badge.svg)](https://github.com/nnsquik/java-project-99/actions/workflows/ci.yml)

# Java Task Manager

## About the Project
A task manager built on Spring Boot. It allows you to manage tasks, statuses, tags, and users.

Менеджер задач на Spring Boot. Позволяет управлять задачами, статусами, метками и пользователями.

## Link to the app
https://java-project-99-60ng.onrender.com/welcome

## How to run locally
```bash
# Clone a repository
git clone https://github.com/nnsquik/java-project-99.git
cd java-project-99

# Generate RSA keys
mkdir -p src/main/resources/certs
openssl genrsa -out src/main/resources/certs/private.pem 2048
openssl rsa -in src/main/resources/certs/private.pem -pubout -out src/main/resources/certs/public.pem

# Run
./gradlew bootRun --args='--spring.profiles.active=development'
```

## Login Information
- Email: hexlet@example.com
- Password: qwerty

## API Documentation
https://java-project-99-60ng.onrender.com/swagger-ui/index.html

## Requirements
-   Java 21+
-   Gradle 8+
