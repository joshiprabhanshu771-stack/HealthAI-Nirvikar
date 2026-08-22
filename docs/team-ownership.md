# Database Team Ownership

This document defines ownership over database tables and who is responsible for managing specific Flyway migrations. 
All changes to the database structure must be done through Flyway migrations. 

## Table Ownership

### Users & Authentication
**Owner:** Prabhanshu
- Manages the `users` table and related authentication schemas.
- Responsible for `V1__create_users_table.sql`.
- If new columns for users (e.g. `phone_number`) are needed, Prabhanshu should create a new migration (e.g., `V4__add_phone_to_users.sql`).

### Health Tips
**Owner:** Meenal
- Manages the `health_tips` table and seed data.
- Responsible for `V2__create_health_tips_table.sql` and `V100__seed_health_tips.sql`.
- If new categories or types of tips are added, Meenal manages those migrations.

### Diseases / Disease Module
**Owner:** Mahima
- Manages the upcoming `diseases` table.
- Responsible for `V3__create_diseases_table.sql`.
- Will define the exact structure of the diseases schema when the module is built.

## Migration Coordination
- When adding a new migration, coordinate with the team to pick the next sequential version number (`V4`, `V5`, etc.).
- Never modify an existing migration file once it is pushed.
- Review each other's SQL scripts via GitHub Pull Requests.