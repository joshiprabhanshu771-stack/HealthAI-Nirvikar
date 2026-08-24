# API Documentation

Base URL: `http://localhost:8080`

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/health-tips` | List all health tips |
| GET | `/api/health-tips/today` | Return a random health tip |
| GET | `/api/health-tips/category/{category}` | Filter by category |
| GET | `/api/health-tips/search?q={query}` | Search health tips |
| GET | `/api/health-tips/{id}` | Find one health tip |

Legacy page paths redirect to the independently running frontend: `/`, `/dashboard`, `/home`, `/login`, `/signup`, `/wellness`, `/wellness/tips`, and `/health-tips`.