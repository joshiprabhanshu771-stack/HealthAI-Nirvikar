# Database Documentation

The application uses MySQL database `healthai_db`. The `health_tips` table is defined in `database/schema.sql` and populated by `database/seed.sql`.

The JDBC repository retains its fallback tip so the health-tip API remains usable when MySQL is unavailable during local development.