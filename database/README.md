# Database Module

This module previously held the raw SQL files for local development.
**We have now migrated to Flyway for automated schema management.**

The SQL files in this folder are kept for legacy reference only. Do **not** use them to create your database schema manually.

## Documentation
Please refer to the new documentation for setting up and managing your local database:
- [Database Documentation](../docs/database-documentation.md)
- [Team Ownership](../docs/team-ownership.md)

Your migrations are located in `backend/src/main/resources/db/migration/`.