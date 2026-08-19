# HealthAI-Nirvikar

This project is split into separate backend and frontend areas to make the system easier for a multi-developer team to work on.

## Project structure

- backend/: Spring Boot application and server-side logic
- frontend/: frontend module for UI development
- database/: SQL scripts for schema and seed data
- docs/: migration and project notes

## Backend folder purpose

- backend/src/main/java/com/healthai/config/: Spring configuration and bean setup
- backend/src/main/java/com/healthai/controller/: REST and MVC controllers
- backend/src/main/java/com/healthai/dto/: request/response DTOs
- backend/src/main/java/com/healthai/entity/: database entities
- backend/src/main/java/com/healthai/repository/: persistence access layer
- backend/src/main/java/com/healthai/service/: business logic interfaces
- backend/src/main/java/com/healthai/service/impl/: service implementations
- backend/src/main/java/com/healthai/exception/: exception handling
- backend/src/main/java/com/healthai/security/: authentication and authorization code
- backend/src/main/java/com/healthai/util/: helper and utility classes
- backend/src/main/java/com/healthai/mapper/: entity-to-DTO mapping logic
- backend/src/main/java/com/healthai/constants/: application-wide constants

## Frontend folder purpose

- frontend/src/: frontend source code
- frontend/public/: static public assets

## Run backend

```bash
cd backend
./mvnw spring-boot:run
```

## Run frontend

```bash
cd frontend
npm install
npm run dev
```

## Database

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
```

## Notes

The current app preserves the existing functionality while making the source layout easier to understand and scale.
