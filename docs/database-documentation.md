# Database Documentation for HealthAI-Nirvikar

This document describes the shared database setup, migration strategy, and development workflow for the team.

## 1. Local MySQL Setup
We DO NOT host a central MySQL database (no AWS RDS, no Supabase, etc.).
Each developer runs **MySQL locally** on their own machine.
- Port: `3306` (default)
- Database Name: `healthai_db`

Do not commit your real database passwords to GitHub! Use environment variables or rely on local properties overriding `application.properties`.
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## 2. Database Name
All developers must standardize on: **`healthai_db`**
Do not use `health_ai_system` or `test`. Use `healthai_db` to ensure your Flyway migrations run successfully on everyone's machines.

## 3. Flyway Purpose
Flyway is a database schema migration tool. It allows the team to synchronize database *schema changes* (tables, columns) through GitHub without sharing actual data. 
- It tracks which SQL scripts have run on your local MySQL.
- When Spring Boot starts, Flyway automatically runs any pending SQL scripts.

## 4. Migration Naming Convention
All migrations are located in: `backend/src/main/resources/db/migration/`
They MUST follow this exact naming format:
`V<VersionNumber>__<Description>.sql` (Note the **double underscore**)

Examples:
- `V1__create_users_table.sql`
- `V2__create_health_tips_table.sql`
- `V4__add_phone_to_users.sql`

## 5. How to Create a Migration
When you need to change the schema (e.g., add a table, add a column):
1. **Never** manually alter your local database and just push Java code.
2. Create a new file in `backend/src/main/resources/db/migration/`.
3. Pick the next available version number (e.g., `V4__add_email.sql`). Coordinate with the team to avoid version conflicts.
4. Write the raw SQL (`ALTER TABLE...` or `CREATE TABLE...`) inside the file.
5. If creating a new table, use `CREATE TABLE IF NOT EXISTS ...`.
6. Start Spring Boot. Flyway will apply it automatically.

**CRITICAL RULE:** NEVER edit a migration file after it has been committed and pushed to GitHub. If you made a mistake, create a new migration (e.g., `V5__fix_users_table.sql`) to fix it.

## 6. How to Run Spring Boot
Run Spring Boot using Maven:
```bash
# In the backend directory
mvnw clean compile
mvnw spring-boot:run
```
Flyway will automatically execute on startup.

## 7. How to Verify Migrations
To verify that Flyway successfully applied migrations, log in to your local MySQL:
```sql
USE healthai_db;
SHOW TABLES;
```
You should see a table called `flyway_schema_history`. You can query it to see the migration status:
```sql
SELECT installed_rank, version, description, type, script, success FROM flyway_schema_history;
```

## 8. Git Workflow
1. Developer creates a new `V<N>__....sql` migration on their branch.
2. Test it locally by starting Spring Boot.
3. `git add backend/src/main/resources/db/migration/...`
4. `git commit -m "Add new migration for X"`
5. `git push origin branch-name`
6. Create a Pull Request and merge to `main`.

## 9. What Happens After `git pull`
When other developers run `git pull` and get your new SQL file, they simply start their Spring Boot application. Flyway detects the new file and automatically applies the SQL statements to their local `healthai_db`. 

## 10. Schema Synchronization vs Data Synchronization
- **Schema Synchronization**: We synchronize table structures via Flyway (e.g. `CREATE TABLE users`).
- **Data Synchronization**: We DO NOT synchronize row data (e.g. `INSERT INTO users`). Everyone has their own local test users. The only exception is universal seed data (like the health tips catalogue).

## 11. What NOT to do
- **DO NOT** edit an existing migration file after pushing it to GitHub.
- **DO NOT** use `spring.jpa.hibernate.ddl-auto=create` or `update`. We use Flyway for DDL. Hibernate is set to `validate`.
- **DO NOT** manually create tables in MySQL without a Flyway migration file.
- **DO NOT** commit actual production passwords.

## 12. How to Handle Existing Databases (Baseline Strategy)
If you already had a `healthai_db` with existing tables before Flyway was introduced, Flyway is configured with `spring.flyway.baseline-on-migrate=true`.
This means Flyway will recognize your existing tables, mark them as baseline, and will gracefully apply `CREATE TABLE IF NOT EXISTS` for missing tables without destroying your existing data.

## 13. How to Handle Migration Conflicts
If two developers create `V4__featureA.sql` and `V4__featureB.sql` on different branches:
1. The first one merged to `main` keeps `V4`.
2. The second developer must rename their file to `V5__featureB.sql` after pulling from `main`, before they merge.
3. If you accidentally get a checksum mismatch error from Flyway, coordinate with the team to resolve the conflict (often by renaming the version or fixing the checksum).