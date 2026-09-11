# HealthAI-Nirvikar Folder Explanation

The project is divided into independently maintained backend, frontend, database, and documentation modules.

## Root structure

```text
HealthAI-Nirvikar/
├── .mvn/              Maven Wrapper support files
├── .vscode/           VS Code workspace settings
├── backend/          Spring Boot API and server-side logic
├── frontend/         Vite frontend pages, styles, and scripts
├── database/         Canonical SQL schema, seed data, and migrations
├── docs/             Architecture, API, database, and migration documentation
├── README.md         Project overview and setup notes
├── folderexplain.md  Detailed folder and file guide
└── .gitignore        Files excluded from Git
```

Generated folders such as `backend/target/`, `frontend/dist/`, and
`frontend/node_modules/` are created locally by builds or dependency
installation and are excluded from the documented source structure.

## Backend

`backend/` is the independently runnable Spring Boot application.

### Backend project files

- `backend/pom.xml` - Maven project definition, dependencies, Java version, and build plugins.
- `backend/mvnw` - Unix Maven Wrapper launcher.
- `backend/mvnw.cmd` - Windows Maven Wrapper launcher.
- `backend/src/main/resources/application.properties` - Server port, database settings, frontend URL, CORS origin, and logging configuration.
- `backend/src/main/resources/db/migration/` - Versioned Flyway migrations:
  - `V1__create_users_table.sql` - Users schema.
  - `V2__create_health_tips_table.sql` - Health tips schema.
  - `V3__create_diseases_table.sql` - Disease module schema.
  - `V4__create_pregnancy_care_tables.sql` - Pregnancy care trimester, nutrition, and warning sign schema.
  - `V5__create_child_health_tables.sql` - Child health milestones, vaccines, and illness protocol schema.
  - `V100__seed_health_tips.sql` - Health tips seed catalog.
  - `V101__seed_pregnancy_care.sql` - Pregnancy care seed data.
  - `V102__seed_child_health.sql` - Child health developmental milestones, vaccine schedule, and illness guide seed data.

### Backend Java package

All Java code is under `backend/src/main/java/com/healthai/`.

- `HealthAIApplication.java` - Spring Boot application entry point.
- `config/` - Spring infrastructure configuration.
- `config/WebConfig.java` - Enables CORS for frontend requests to `/api/**`.
- `config/FlywayConfig.java` - Flyway migration configuration.
- `controller/` - HTTP endpoint controllers.
- `controller/HealthWellnessController.java` - Health-tip REST API endpoints for listing, searching, filtering, random selection, and lookup by ID.
- `controller/PregnancyCareController.java` - Pregnancy Care REST API endpoints for trimesters, nutrition, warning signs, and gestational due date calculations.
- `controller/ChildHealthController.java` - Child Health REST API endpoints for developmental milestones, vaccines, and illness protocols.
- `controller/PageController.java` - Redirects legacy page URLs to the independently running frontend.
- `dto/` - Request and response Data Transfer Objects.
- `dto/PregnancyCalculationResult.java` - DTO holding due date, current gestational week, trimester, and baby growth metrics.
- `entity/` - Classes representing persisted domain data.
- `entity/HealthTip.java` - Domain object representing a row in the `health_tips` table.
- `entity/PregnancyWeekGuide.java` - Domain object representing pregnancy trimester guides.
- `entity/PregnancyNutrition.java` - Domain object representing prenatal nutrients and food safety.
- `entity/PregnancyWarningSign.java` - Domain object representing maternal red flags.
- `entity/ChildMilestone.java` - Domain object representing pediatric growth milestones by age.
- `entity/ChildVaccine.java` - Domain object representing universal childhood immunization records.
- `entity/ChildIllnessGuide.java` - Domain object representing common childhood illness protocols.
- `repository/` - Persistence contracts and implementations with offline fallback data.
- `repository/HealthTipRepository.java` - Repository contract for health-tip operations.
- `repository/JdbcHealthTipRepository.java` - JDBC implementation for health tips.
- `repository/PregnancyCareRepository.java` - Repository contract for pregnancy care operations.
- `repository/JdbcPregnancyCareRepository.java` - JDBC implementation for pregnancy care with built-in fallback data.
- `repository/ChildHealthRepository.java` - Repository contract for child health operations.
- `repository/JdbcChildHealthRepository.java` - JDBC implementation for child health with built-in fallback data.
- `service/` - Business service interfaces.
- `service/HealthTipService.java` - Service contract for health-tip operations.
- `service/PregnancyCareService.java` - Service contract for pregnancy care operations and calculations.
- `service/ChildHealthService.java` - Service contract for child health operations.
- `service/impl/` - Business service implementations.
- `service/impl/HealthTipServiceImpl.java` - Delegates health-tip operations to the repository layer.
- `service/impl/PregnancyCareServiceImpl.java` - Pregnancy Care calculations and data retrieval.
- `service/impl/ChildHealthServiceImpl.java` - Delegates child health operations to the repository layer.
- `util/` - Shared backend helper classes.
- `util/DBConnection.java` - Creates and closes JDBC connections using application configuration.

## Frontend

`frontend/` is the independently runnable Vite application.

- `frontend/package.json` - Frontend metadata and `dev`, `build`, and `preview` scripts.
- `frontend/package-lock.json` - Locked npm dependency versions.
- `frontend/vite.config.js` - Vite server configuration and `/api` proxy to `http://localhost:8080`.
- `frontend/index.html` - Vite entry page that opens the dashboard page.
- `frontend/src/pages/` - Complete user-facing HTML pages.
- `frontend/src/pages/health_and_wellness.html` - Health and wellness 4-option hub navigation page.
- `frontend/src/pages/health_tips.html` - Health tips page that loads data from the REST API.
- `frontend/src/pages/pregnancy_care.html` - Dedicated Pregnancy Care module.
- `frontend/src/pages/child_health.html` - Dedicated Child Health module with developmental milestone explorer, vaccine tracker, pediatric fever triage tool, and choking protocols.
- `frontend/src/pages/login.html` - Login page.
- `frontend/src/pages/signup.html` - Account registration page.
- `frontend/src/pages/user_dashboard.html` - Main HealthAI dashboard page.
- `frontend/src/assets/css/user_dashboard.css` - Dashboard and shared layout styles.
- `frontend/src/assets/css/health_tips.css` - Health tips page styles.
- `frontend/src/assets/css/pregnancy_care.css` - Pregnancy care module styles.
- `frontend/src/assets/css/child_health.css` - Child health module styles.
- `frontend/src/assets/js/health_tips.js` - Fetches and renders health tips.
- `frontend/src/assets/js/pregnancy_care.js` - Due date calculator and trimester guide engine.
- `frontend/src/assets/js/child_health.js` - Milestone tabs, vaccine filter, and pediatric fever triage engine.
- `frontend/src/services/api.js` - Shared frontend API client.

## Database

`database/` is the single source of truth for SQL files.

- `database/README.md` - Database setup instructions and module purpose.
- `database/schema.sql` - Creates the `healthai_db` database and initial tables.
- `database/seed.sql` - Inserts initial seed data.

## Run the backend

From the repository root in PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The backend runs on `http://localhost:8080`.

## Run the frontend

From the repository root in PowerShell:

```powershell
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:5173` and proxies `/api` requests to the backend.
