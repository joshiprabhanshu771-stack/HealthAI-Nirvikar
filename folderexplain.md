# HealthAI-Nirvikar Folder Explanation

The project is divided into independently maintained backend, frontend, database, and documentation modules.

## Root structure

```text
HealthAI-Nirvikar/
├── .mvn/              Maven Wrapper support files
├── .vscode/           VS Code workspace settings
├── backend/           Spring Boot API and server-side logic
├── frontend/          Static frontend pages, styles, and scripts
├── database/          Reference SQL scripts and legacy archive
├── docs/              Architecture, API, database, and migration documentation
├── README.md          Project overview and setup notes
├── folderexplain.md   Detailed folder and file guide
└── .gitignore         Files excluded from Git
```

Generated folders such as `backend/target/` are created locally by builds
and are excluded from the documented source structure.

## Backend

`backend/` is the independently runnable Spring Boot application.

### Backend project files

- `backend/pom.xml` - Maven project definition, dependencies (Jackson Databind, Spring Web, JPA), Java version, and build plugins.
- `backend/mvnw` - Unix Maven Wrapper launcher.
- `backend/mvnw.cmd` - Windows Maven Wrapper launcher.
- `backend/src/main/resources/application.properties` - Server port, database settings, frontend URL, CORS origin, and logging configuration.
<<<<<<< HEAD
- `backend/src/main/resources/db/migration/` - Flyway versioned migration scripts applied automatically at startup.
  - `V1__create_users_table.sql` - Creates the `users` table.
  - `V2__create_health_tips_table.sql` - Creates the `health_tips` table.
  - `V3__create_diseases_table.sql` - Creates the `diseases` table.
  - `V100__seed_health_tips.sql` - Inserts the initial health-tip seed data.
=======
- `backend/src/main/resources/db/migration/` - Versioned Flyway migrations:
  - `V1__create_users_table.sql` - Users schema.
  - `V2__create_health_tips_table.sql` - Health tips schema.
  - `V3__create_diseases_table.sql` - Disease module schema.
  - `V4__create_pregnancy_care_tables.sql` - Pregnancy care trimester, nutrition, and warning sign schema.
  - `V5__create_child_health_tables.sql` - Child health milestones, vaccines, and illness protocol schema.
  - `V6__create_blood_donation_tables.sql` - Blood donors, emergency requests, and blood bank schema.
  - `V100__seed_health_tips.sql` - Health tips seed catalog.
  - `V101__seed_pregnancy_care.sql` - Pregnancy care seed data.
  - `V102__seed_child_health.sql` - Child health developmental milestones, vaccine schedule, and illness guide seed data.
  - `V103__seed_blood_donation.sql` - Blood donor records, active emergency requests, and regional blood bank seed data.
>>>>>>> meenal

### Backend Java package

All Java code is under `backend/src/main/java/com/healthai/`.

- `HealthAIApplication.java` - Spring Boot application entry point.
- `config/` - Spring infrastructure configuration.
<<<<<<< HEAD
  - `config/WebConfig.java` - Enables CORS for frontend requests to `/api/**`.
  - `config/FlywayConfig.java` - Configures Flyway for database migration management.
  - `config/package-info.java` - Documents the configuration package purpose.
- `controller/` - HTTP endpoint controllers.
  - `controller/HealthWellnessController.java` - Health-tip REST API endpoints for listing, searching, filtering, random selection, and lookup by ID.
  - `controller/PageController.java` - Redirects legacy page URLs to the independently running frontend.
- `dto/` - Reserved for API request and response Data Transfer Objects.
  - `dto/package-info.java` - Documents the DTO package purpose.
- `entity/` - Classes representing persisted domain data.
  - `entity/HealthTip.java` - Domain object representing a row in the `health_tips` table.
- `repository/` - Persistence contracts and implementations.
  - `repository/HealthTipRepository.java` - Repository contract for health-tip operations.
  - `repository/JdbcHealthTipRepository.java` - JDBC implementation with database access and fallback health-tip data.
- `service/` - Business service interfaces.
  - `service/HealthTipService.java` - Service contract for health-tip operations.
  - `service/impl/` - Business service implementations.
  - `service/impl/HealthTipServiceImpl.java` - Delegates health-tip operations to the repository layer.
- `exception/` - Reserved for domain exceptions and global error handling.
  - `exception/package-info.java` - Documents the exception package purpose.
- `security/` - Reserved for authentication and authorization code.
  - `security/package-info.java` - Documents the security package purpose.
- `mapper/` - Reserved for entity-to-DTO and DTO-to-entity mappings.
  - `mapper/package-info.java` - Documents the mapper package purpose.
- `constants/` - Reserved for shared application constants.
  - `constants/package-info.java` - Documents the constants package purpose.
- `util/` - Shared backend helper classes.
  - `util/DBConnection.java` - Creates and closes JDBC connections using application configuration.
=======
- `config/WebConfig.java` - Enables CORS for frontend requests to `/api/**`.
- `config/FlywayConfig.java` - Flyway migration configuration.
- `controller/` - HTTP endpoint controllers.
- `controller/HealthWellnessController.java` - Health-tip REST API endpoints.
- `controller/PregnancyCareController.java` - Pregnancy Care REST API endpoints.
- `controller/ChildHealthController.java` - Child Health REST API endpoints.
- `controller/BloodDonationController.java` - Blood donation, donor registration, and emergency requests REST API.
- `controller/ThirdPartyHealthController.java` - OpenFDA and NIH MedlinePlus external medical REST API.
- `controller/PageController.java` - Redirects legacy page URLs to the independently running frontend.
- `dto/` - Request and response Data Transfer Objects.
- `dto/PregnancyCalculationResult.java` - DTO holding due date, gestational week, and trimester metrics.
- `entity/` - Classes representing persisted domain data.
- `entity/HealthTip.java` - Domain object representing a health tip.
- `entity/PregnancyWeekGuide.java` - Domain object representing pregnancy trimester guides.
- `entity/PregnancyNutrition.java` - Domain object representing prenatal nutrients and food safety.
- `entity/PregnancyWarningSign.java` - Domain object representing maternal red flags.
- `entity/ChildMilestone.java` - Domain object representing pediatric growth milestones by age.
- `entity/ChildVaccine.java` - Domain object representing universal childhood immunization records.
- `entity/ChildIllnessGuide.java` - Domain object representing common childhood illness protocols.
- `entity/BloodDonor.java` - Domain object representing a registered blood donor.
- `entity/BloodRequest.java` - Domain object representing an emergency blood requirement.
- `entity/BloodBank.java` - Domain object representing a verified blood storage bank.
- `repository/` - Persistence contracts and implementations with offline fallback data.
- `repository/HealthTipRepository.java` / `JdbcHealthTipRepository.java`
- `repository/PregnancyCareRepository.java` / `JdbcPregnancyCareRepository.java`
- `repository/ChildHealthRepository.java` / `JdbcChildHealthRepository.java`
- `repository/BloodDonationRepository.java` / `JdbcBloodDonationRepository.java`
- `service/` - Business service interfaces and implementations.
- `service/HealthTipService.java` / `impl/HealthTipServiceImpl.java`
- `service/PregnancyCareService.java` / `impl/PregnancyCareServiceImpl.java`
- `service/ChildHealthService.java` / `impl/ChildHealthServiceImpl.java`
- `service/BloodDonationService.java` / `impl/BloodDonationServiceImpl.java`
- `service/ThirdPartyHealthService.java` / `impl/ThirdPartyHealthServiceImpl.java`
- `util/DBConnection.java` - Creates and closes JDBC connections.
>>>>>>> meenal

## Frontend

`frontend/` contains the static frontend pages, styles, scripts, and services.

<<<<<<< HEAD
- `frontend/index.html` - Entry redirect to the main user dashboard.
- `frontend/pages/` - User-facing HTML pages:
  - `pages/user_dashboard.html` - Main HealthAI dashboard page.
  - `pages/disease.html` - Complete Disease Information Library with search, category filtering, and pagination.
  - `pages/disease-detail.html` - Dynamic disease detail page displaying clinical sections and medical sources.
  - `pages/health_and_wellness.html` - Health and wellness navigation page.
  - `pages/health_tips.html` - Health tips page that loads data from the REST API.
  - `pages/login.html` - Login page.
  - `pages/signup.html` - Account registration page.
- `frontend/assets/` - Single centralized frontend assets directory (no duplicate folders).
  - `assets/css/` - Consolidated CSS stylesheets:
    - `assets/css/disease.css` - Disease directory layout, design tokens, badges, and components.
    - `assets/css/disease-vanilla.css` - Vanilla JS disease cards, status, pagination, and detail views.
    - `assets/css/user_dashboard.css` - Dashboard layout and components.
    - `assets/css/health_tips.css` - Health tips page styles.
    - `assets/css/style.css` - Shared authentication and base styles.
  - `assets/js/` - Consolidated Vanilla JavaScript modules:
    - `assets/js/api.js` - Unified REST API client for Diseases, Categories, and Health Tips.
    - `assets/js/diseases.js` - Disease listing, category chips, debounced search, and pagination.
    - `assets/js/disease-detail.js` - Dynamic disease detail rendering with conditional section visibility.
    - `assets/js/health_tips.js` - Health tips interactive client engine.
=======
- `frontend/package.json` - Frontend metadata and build scripts.
- `frontend/package-lock.json` - Locked npm dependency versions.
- `frontend/vite.config.js` - Vite server configuration and `/api` proxy.
- `frontend/index.html` - Vite entry page.
- `frontend/src/pages/health_and_wellness.html` - Health & Wellness 4-option hub navigation page.
- `frontend/src/pages/health_tips.html` - Health tips interactive page.
- `frontend/src/pages/pregnancy_care.html` - Pregnancy care module page.
- `frontend/src/pages/child_health.html` - Child health and pediatric triage page.
- `frontend/src/pages/blood_donation.html` - Blood donation, donor registration, emergency requests, compatibility matrix, and eligibility quiz portal.
- `frontend/src/pages/user_dashboard.html` - Main HealthAI dashboard.
- `frontend/src/assets/css/user_dashboard.css` - Main dashboard stylesheet.
- `frontend/src/assets/css/health_tips.css` - Health tips stylesheet.
- `frontend/src/assets/css/pregnancy_care.css` - Pregnancy care stylesheet.
- `frontend/src/assets/css/child_health.css` - Child health stylesheet.
- `frontend/src/assets/css/blood_donation.css` - Blood donation portal stylesheet.
- `frontend/src/assets/js/health_tips.js` - Health tips interactive engine.
- `frontend/src/assets/js/pregnancy_care.js` - Pregnancy care engine.
- `frontend/src/assets/js/child_health.js` - Child health engine.
- `frontend/src/assets/js/blood_donation.js` - Blood donation, live search, compatibility matrix, and registration engine.
- `frontend/src/services/api.js` - Unified frontend API client.
>>>>>>> meenal

## Database

`database/` contains reference SQL files and archived legacy scripts.

- `database/README.md` - Database setup instructions and module purpose.
<<<<<<< HEAD
- `database/legacy/` - Legacy SQL scripts preserved for reference.
  - `legacy/Nirvikar_HealthAI_system.sql` - Original legacy database dump.
  - `legacy/schema.sql` - Legacy schema definition.
  - `legacy/seed.sql` - Legacy seed data.

> **Note:** Active database migrations are managed by Flyway from
> `backend/src/main/resources/db/migration/`. The files in `database/legacy/`
> are kept for historical reference only.

## Documentation

- `docs/architecture.md` - Explains module boundaries and runtime communication.
- `docs/api-documentation.md` - Lists backend API endpoints and legacy redirects.
- `docs/database-documentation.md` - Documents the MySQL database and fallback behavior.
- `docs/migration-plan.md` - Complete old-location to new-location migration report.
- `docs/team-ownership.md` - Maps frontend, backend, database, and AI/ML responsibilities.
=======
- `database/schema.sql` - Creates the `healthai_db` database and initial tables.
- `database/seed.sql` - Inserts initial seed data.
>>>>>>> meenal

## Run the backend

From the repository root in PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The backend runs on `http://localhost:8080`.

## Run the frontend

Open `frontend/index.html` directly in a browser, or serve it with any static file server.
For a quick local server using Node.js:

```powershell
cd frontend
npx serve .
```

<<<<<<< HEAD
## Build commands

Backend compile and package:

```powershell
cd backend
.\mvnw.cmd package -DskipTests
```
=======
The frontend runs on `http://localhost:5173` and proxies `/api` requests to the backend.
>>>>>>> meenal
