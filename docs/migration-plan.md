# Migration Plan

## 1. Project layout

The project was restructured from a single backend-focused Maven app into a clearer multi-module layout:

- backend/: Spring Boot application and Java source
- frontend/: separate frontend module for UI work
- database/: SQL schema and seed scripts
- docs/: project documentation
- README.md: project startup and team guide

# Migration Report

## Old location -> New location

`src/main/java/com/healthai/HealthAI.java`
↓
`backend/src/main/java/com/healthai/HealthAIApplication.java`

`src/main/java/com/healthai/model/HealthTip.java`
↓
`backend/src/main/java/com/healthai/entity/HealthTip.java`

`src/main/java/com/healthai/dao/HealthTipDAO.java`
↓
`backend/src/main/java/com/healthai/repository/HealthTipRepository.java`

`src/main/java/com/healthai/dao/HealthTipDAOImpl.java`
↓
`backend/src/main/java/com/healthai/repository/JdbcHealthTipRepository.java`

`src/main/java/com/healthai/controller/*`
↓
`backend/src/main/java/com/healthai/controller/*`

`src/main/java/com/healthai/util/DBConnection.java`
↓
`backend/src/main/java/com/healthai/util/DBConnection.java`

`src/main/resources/templates/*`
↓
`frontend/src/pages/*`

`src/main/resources/static/css/*`
↓
`frontend/src/assets/css/*`

`src/main/resources/static/js/*`
↓
`frontend/src/assets/js/*`

`src/main/resources/cssfiles/user_dashboard.css`
↓
`frontend/src/assets/css/user_dashboard.css`

`src/main/resources/sql/health_tips_schema.sql`
↓
`database/schema.sql`

`src/main/resources/schema.sql`
↓
`database/schema.sql` (duplicate removed; one canonical copy retained)

## Package decisions

The old `model` package contained the `HealthTip` persistence object, so it is now an `entity`. The old DAO contract and implementation are now `repository` types. No DTO or mapper classes existed in the baseline, so those extension packages remain empty and documented rather than inventing duplicate types.

Thymeleaf was removed because pages now run under Vite. Existing page URLs remain available through backend redirects, and health-tip data remains available through the unchanged `/api/health-tips` endpoints.
