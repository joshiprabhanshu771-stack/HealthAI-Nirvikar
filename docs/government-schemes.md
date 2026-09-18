# Government Health Schemes Module Documentation
**Project:** HealthAI - Nirvikar  
**Module:** Government Health Schemes Ingestion & Integration

---

## 1. Data Source Overview

The HealthAI Government Health Schemes module is sourced exclusively from official Government of India and State Government healthcare portals and initiatives, including:
- **myScheme.gov.in** — National Government Schemes Discovery Platform (Ministry of Electronics & IT / Digital India).
- **National Health Mission (NHM)** (`nhm.gov.in`) — Ministry of Health & Family Welfare (MoHFW).
- **National Health Authority (NHA)** (`pmjay.gov.in`, `abdm.gov.in`) — Ayushman Bharat Pradhan Mantri Jan Arogya Yojana & Digital Health Mission.
- **Ministry of Women & Child Development (MoWCD)** (`wcd.gov.in`) — PMMVY.
- **Central Government Health Portals** (`cghs.nic.in`, `echs.gov.in`, `tbcindia.gov.in`, `telemanas.mohfw.gov.in`, `naco.gov.in`, `rbsk.gov.in`, `pmsma.mohfw.gov.in`).
- **Official State Health Departments** (`jeevandayee.gov.in` for Maharashtra, `magujarat.com` for Gujarat, `cmchistn.com` for Tamil Nadu, `aarogyasri.ap.gov.in` for Andhra Pradesh, `bsky.odisha.gov.in` for Odisha, `chiranjeevi.rajasthan.gov.in` for Rajasthan, `sha.kerala.gov.in` for Kerala, `sacchu.up.gov.in` for Uttar Pradesh, `ayushmanmp.in` for Madhya Pradesh, `arogya.karnataka.gov.in` for Karnataka, `swasthyasathi.gov.in` for West Bengal, `delhi.gov.in` for Delhi, `atalamritabhiyan.assam.gov.in` for Assam, `goa.gov.in` for Goa).

---

## 2. Why These Sources Were Selected

1. **Healthcare Focus**: HealthAI is a digital healthcare platform. Sourcing specifically health and wellness schemes ensures user relevance, excluding unrelated schemes such as agricultural subsidies, rural employment, and census datasets.
2. **Authority & Legitimacy**: Information and URLs are official `.gov.in` / `.nic.in` or recognized statutory state health portals, ensuring citizens receive genuine entitlements without commercial or misleading third-party content.
3. **Accuracy**: Eligibility criteria, program descriptions, and scheme categories are strictly based on government gazettes and policy documents.

---

## 3. Architecture: API vs. Dataset Snapshot

- **Primary Mode (Reliable Verified Snapshot)**: Ingested and stored as a structured JSON snapshot (`data/government_schemes.json`) served via the Spring Boot backend (`GET /api/government-schemes`).
- **Live Official API Extensibility**: Built-in architecture supports external official APIs (e.g. **API Setu** / **myScheme API**) configurable via `application.properties`. When enabled and credentials are provided, Spring Boot proxies requests to the live API. If disabled or unreachable, it falls back seamlessly to the verified snapshot.

```
Official Sources (myScheme / NHM / MoHFW / State Portals)
                         │
                         ▼
        tools/government-schemes/health_schemes.csv
                         │
                         ▼ (Python Normalizer / Ingestion Tool)
 ┌────────────────────────────────────────────────────────┐
 │   backend/src/main/resources/data/government_schemes.json   │
 │   frontend/data/government_schemes.json                │
 └────────────────────────────────────────────────────────┘
                         │
                         ▼
       Spring Boot REST API (GET /api/government-schemes)
                         │
                         ▼
  HTML5 + CSS3 + Vanilla JavaScript Frontend (with Pagination & Search)
```

---

## 4. How Data is Imported

The data ingestion pipeline uses a robust Python script located at `tools/government-schemes/convert_dataset.py`:

```bash
# Run data conversion from the project root:
python tools/government-schemes/convert_dataset.py
```

### Ingestion Logic:
- Reads CSV (`health_schemes.csv`) or JSON datasets.
- Automatically normalizes whitespace and strips formatting anomalies.
- Deduplicates schemes based on name comparison.
- Enforces strict data quality rules:
  - Discards rows without a valid scheme name.
  - Validates official URLs (only valid `http://` / `https://` schemas accepted).
  - Supplies standard fallback text when eligibility criteria is missing.
- Automatically synchronizes output to both:
  - `backend/src/main/resources/data/government_schemes.json`
  - `frontend/data/government_schemes.json`

---

## 5. JSON Data Model

```json
[
  {
    "id": 1,
    "scheme_name": "Ayushman Bharat - Pradhan Mantri Jan Arogya Yojana (AB-PMJAY)",
    "short_description": "Provides cashless secondary and tertiary hospitalization coverage of up to Rs. 5 lakh per family per year to poor and vulnerable families.",
    "category": "Health Insurance",
    "scheme_type": "Central Scheme",
    "state": "All India",
    "eligibility": "Families identified based on SECC 2011 deprivation and occupational criteria for rural and urban areas, RSBY beneficiaries, and senior citizens aged 70+ irrespective of income.",
    "official_url": "https://pmjay.gov.in"
  }
]
```

### Supported Categories:
- `Health Insurance`
- `Women & Maternal Health`
- `Child Health`
- `Senior Citizens`
- `Treatment & Medical Assistance`
- `Immunization`
- `Disease Control`
- `Mental Health`
- `Disability Health Support`
- `Public Health`
- `Blood & Health Support`
- `Social Welfare`

---

## 6. Backend REST API

### Endpoint:
```http
GET /api/government-schemes
```

### Response Headers:
```http
Content-Type: application/json;charset=UTF-8
Access-Control-Allow-Origin: *
```

### Controller:
`backend/src/main/java/com/healthai/controller/GovernmentSchemeController.java`

- Reads UTF-8 encoded JSON safely using `ClassPathResource`.
- Never exposes internal filesystem paths in error messages.
- Returns `HTTP 503` or `HTTP 500` with JSON error payload if data cannot be loaded.
- Handles external API timeout and error fallback.

---

## 7. Frontend Flow & Features

- **Dynamic Filters**: Dropdowns for **Category**, **Scheme Type**, and **State** are dynamically generated from available dataset entries at runtime.
- **Multi-Field Search**: Real-time search across scheme names, descriptions, categories, scheme types, states, and eligibility criteria.
- **Category Buttons**: Clickable category pills that synchronize with dropdown selections.
- **Pagination ("Load More")**: Renders 20 schemes per batch by default. Clicking "Load More Schemes" smoothly appends the next batch without re-rendering existing items.
- **Live Counter**: Displays current view statistics, e.g. `(Showing 20 of 34 schemes)`.
- **View All Schemes**: Resets all search filters, category buttons, dropdowns, and returns to the initial batch of all schemes.
- **Security**: Built-in HTML sanitization (`escapeHtml()`) prevents XSS attacks. External URLs open in new tabs with `target="_blank"` and `rel="noopener noreferrer"`.
- **Details Modal**: Displays full details, eligibility criteria, official links, and source attribution.

---

## 8. How to Update the Dataset

1. Add or edit rows in `tools/government-schemes/health_schemes.csv`.
2. Run the ingestion tool:
   ```bash
   python tools/government-schemes/convert_dataset.py
   ```
3. Rebuild/restart the Spring Boot backend or refresh the frontend.

---

## 9. Required API Credentials & Setup for Live API Setu Integration

To enable direct live streaming from API Setu or myScheme API in production:

1. **Register on API Setu**: Sign up as an API consumer at [https://apisetu.gov.in/](https://apisetu.gov.in/).
2. **Obtain API Keys**: Register your application to receive an `API Key` and `Client ID`.
3. **Configure `application.properties` or Environment Variables**:
   ```properties
   gov.schemes.api.enabled=true
   gov.schemes.api.url=https://apisetu.gov.in/api/v1/myscheme/schemes
   gov.schemes.api.api-key=YOUR_API_SETU_KEY
   gov.schemes.api.client-id=YOUR_CLIENT_ID
   gov.schemes.api.timeout.seconds=5
   ```
   Or use environment variables:
   - `GOV_SCHEMES_API_ENABLED=true`
   - `GOV_SCHEMES_API_URL=...`
   - `GOV_SCHEMES_API_KEY=...`
   - `GOV_SCHEMES_CLIENT_ID=...`

---

## 10. Fallback Behavior

- If `gov.schemes.api.enabled` is `false`, the backend instantly serves the local verified snapshot.
- If `gov.schemes.api.enabled` is `true` but the external API is unreachable, times out (default: 5 seconds), or returns a non-2xx status, Spring Boot catches the error, logs a warning, and immediately returns the verified snapshot.
- If both external API and local files are unavailable, the backend returns a structured JSON error response `{"error": "Government schemes catalogue is currently unavailable."}`.
- If the frontend fails to reach the backend, it renders a friendly UI error message with a "Retry Loading" button.

---

## 11. Limitations & Scope

- **Scope**: Covers verified Central and State **Health & Wellness** schemes across India. Non-healthcare schemes (e.g. agricultural subsidies, rural housing) are intentionally excluded.
- **Real-Time Eligibility Checking**: Scheme guidelines and financial thresholds are updated periodically by relevant state and central ministries. Citizens must always verify current guidelines via the provided official links before applying.
