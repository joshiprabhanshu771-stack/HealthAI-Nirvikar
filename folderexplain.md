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

### Backend Java package

All Java code is under `backend/src/main/java/com/healthai/`.

- `HealthAIApplication.java` - Spring Boot application entry point.
- `config/` - Spring infrastructure configuration.
- `config/WebConfig.java` - Enables CORS for frontend requests to `/api/**`.
- `config/package-info.java` - Documents the configuration package purpose.
- `controller/` - HTTP endpoint controllers.
- `controller/HealthWellnessController.java` - Health-tip REST API endpoints for listing, searching, filtering, random selection, and lookup by ID.
- `controller/PageController.java` - Redirects legacy page URLs to the independently running frontend.
- `dto/` - Reserved for API request and response Data Transfer Objects.
- `dto/package-info.java` - Documents the DTO package purpose.
- `entity/` - Classes representing persisted domain data.
- `entity/HealthTip.java` - Domain object representing a row in the `health_tips` table.
- `repository/` - Persistence contracts and implementations; this is the replacement for the old DAO package.
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

The backend no longer contains Thymeleaf templates, static frontend assets, backup folders, or SQL scripts.

## Frontend

`frontend/` is the independently runnable Vite application.

- `frontend/package.json` - Frontend metadata and `dev`, `build`, and `preview` scripts.
- `frontend/package-lock.json` - Locked npm dependency versions.
- `frontend/vite.config.js` - Vite server configuration and `/api` proxy to `http://localhost:8080`.
- `frontend/index.html` - Vite entry page that opens the dashboard page.
- `frontend/public/` - Files served unchanged by Vite.
- `frontend/public/README.md` - Documents the public asset folder.
- `frontend/src/` - Frontend source code.
- `frontend/src/pages/` - Complete user-facing HTML pages.
- `frontend/src/pages/health_and_wellness.html` - Health and wellness navigation page.
- `frontend/src/pages/health_tips.html` - Health tips page that loads data from the REST API.
- `frontend/src/pages/login.html` - Login page.
- `frontend/src/pages/signup.html` - Account registration page.
- `frontend/src/pages/user_dashboard.html` - Main HealthAI dashboard page.
- `frontend/src/assets/` - Frontend styles, scripts, images, and fonts.
- `frontend/src/assets/css/style.css` - Shared authentication and base styles.
- `frontend/src/assets/css/user_dashboard.css` - Dashboard and shared layout styles.
- `frontend/src/assets/css/health_tips.css` - Health tips page styles.
- `frontend/src/assets/js/health_tips.js` - Fetches and renders health tips, filtering, search, cycling, and detail modal behavior.
- `frontend/src/assets/images/` - Frontend image asset location.
- `frontend/src/assets/images/README.md` - Documents the image folder.
- `frontend/src/assets/fonts/` - Frontend font asset location.
- `frontend/src/assets/fonts/README.md` - Documents the font folder.
- `frontend/src/components/` - Reusable frontend components.
- `frontend/src/components/README.md` - Documents the component folder.
- `frontend/src/services/api.js` - Shared frontend API helper for health-tip requests.
- `frontend/src/utils/` - Shared frontend utility functions.
- `frontend/src/utils/README.md` - Documents the utility folder.

## Database

`database/` is the single source of truth for SQL files.

- `database/README.md` - Database setup instructions and module purpose.
- `database/schema.sql` - Creates the `healthai_db` database and `health_tips` table.
- `database/seed.sql` - Inserts initial health-tip data.
- `database/migrations/` - Location for future versioned database changes.

## Documentation

- `docs/architecture.md` - Explains module boundaries and runtime communication.
- `docs/api-documentation.md` - Lists backend API endpoints and legacy redirects.
- `docs/database-documentation.md` - Documents the MySQL database and fallback behavior.
- `docs/migration-plan.md` - Complete old-location to new-location migration report.
- `docs/team-ownership.md` - Maps frontend, backend, database, and AI/ML responsibilities.

## Run the backend

From the repository root in PowerShell:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The backend runs on `http://localhost:8080`.

If the Java environment is not configured, set `JAVA_HOME` to the JDK installation directory first:

```powershell
$env:JAVA_HOME = "C:\Program Files\jdk-25"
```

## Run the frontend

From the repository root in PowerShell:

```powershell
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:5173` and proxies `/api` requests to the backend.

## Build commands

Backend compile:

```powershell
cd backend
.\mvnw.cmd test -DskipTests
```

Frontend production build:

```powershell
cd frontend
npm run build
```
