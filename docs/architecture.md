# Architecture

HealthAI is split into independently runnable modules:

- `backend/` contains the Spring Boot API, business services, repositories, and cross-cutting configuration.
- `frontend/` contains HTML pages, CSS, JavaScript, and frontend-only helpers. Vite proxies `/api` to the backend during development.
- `database/` contains the canonical schema, seed data, and future migrations.
- `docs/` contains architecture, API, database, and migration guidance.

The backend no longer serves Thymeleaf templates or static frontend assets. Legacy browser routes redirect to the configured frontend URL, while data remains available through `/api/health-tips`.