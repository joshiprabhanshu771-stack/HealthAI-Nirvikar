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

- `backend/pom.xml` - Maven project definition, dependencies (Jackson Databind, Spring Web, JPA), Java version, and build plugins.
- `backend/mvnw` - Unix Maven Wrapper launcher.
- `backend/mvnw.cmd` - Windows Maven Wrapper launcher.
- `backend/src/main/resources/application.properties` - Server port, database settings, frontend URL, CORS origin, and logging configuration.
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

### Backend Java package

All Java code is under `backend/src/main/java/com/healthai/`.

- `HealthAIApplication.java` - Spring Boot application entry point.
- `config/` - Spring infrastructure configuration.
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

## Frontend

`frontend/` is the independently runnable Vite application.

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
