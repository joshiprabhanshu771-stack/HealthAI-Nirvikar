# API Documentation

Base URL: `http://localhost:8080`

## Health Tips Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/health-tips` | List all health tips |
| GET | `/api/health-tips/today` | Return a random health tip |
| GET | `/api/health-tips/category/{category}` | Filter by category |
| GET | `/api/health-tips/search?q={query}` | Search health tips |
| GET | `/api/health-tips/{id}` | Find one health tip |

## Pregnancy Care Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/pregnancy-care/trimesters` | List all trimester guidance & developmental milestones |
| GET | `/api/pregnancy-care/trimesters/{number}` | Get guidance for a specific trimester (1, 2, 3) |
| GET | `/api/pregnancy-care/nutrition` | List prenatal vitamins, safe foods & foods to avoid |
| GET | `/api/pregnancy-care/nutrition?category={cat}` | Filter nutrition by category (`Essential`, `Safe Food`, `Avoid Food`) |
| GET | `/api/pregnancy-care/warning-signs` | List maternal red-flag warning signs and emergency protocols |
| GET | `/api/pregnancy-care/calculate?lmp={YYYY-MM-DD}` | Calculate estimated due date, gestational age & stage from LMP |

## Child Health Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/child-health/milestones` | List all age-stage developmental milestones |
| GET | `/api/child-health/milestones/{ageGroup}` | Get milestone details for a specific age group |
| GET | `/api/child-health/vaccines` | List all pediatric vaccines |
| GET | `/api/child-health/vaccines?age={age}` | Filter vaccines by age stage |
| GET | `/api/child-health/illnesses` | List common childhood illnesses and home care protocols |
| GET | `/api/child-health/illnesses?category={cat}` | Filter illness guides by category |

## Blood Donation & Donor Portal Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/api/blood-donation/donors` | Register a new voluntary blood donor |
| GET | `/api/blood-donation/donors` | Search & filter blood donors by blood group, city, and availability |
| POST | `/api/blood-donation/requests` | Post an emergency blood requirement request |
| GET | `/api/blood-donation/requests` | List and filter active emergency blood requests |
| PUT | `/api/blood-donation/requests/{id}/fulfill` | Mark an emergency blood request as fulfilled |
| GET | `/api/blood-donation/banks` | List verified regional blood centres and banks by city |
| GET | `/api/blood-donation/compatibility?bloodGroup={grp}` | Get full red cell & plasma compatibility rules |

## Third-Party Medical & Clinical Endpoints (10,000+ Conditions & Cures)

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/external/disease-search?query={term}&limit={N}` | Live search 10,000+ diseases, indications, medications, and clinical cures via OpenFDA |
| GET | `/api/external/health-topics?term={term}&limit={N}` | Live search thousands of comprehensive medical articles from NIH MedlinePlus |
| GET | `/api/external/pediatric-guidance?condition={term}` | Live search pediatric safety guidelines, child warnings, and dosage |
| GET | `/api/external/pregnancy-safety?query={term}` | Live search maternal, pregnancy, and lactation safety precautions |

Legacy page paths redirect to the independently running frontend: `/`, `/dashboard`, `/home`, `/login`, `/signup`, `/wellness`, `/wellness/tips`, `/health-tips`, `/wellness/pregnancy`, `/pregnancy-care`, `/pregnancy`, `/wellness/child-health`, `/child-health`, `/services/blood-donation`, and `/blood-donation`.