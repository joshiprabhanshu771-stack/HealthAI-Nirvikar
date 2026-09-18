#!/usr/bin/env python3
"""
Government Health Schemes Data Ingestion & Normalizer Tool
HealthAI - Nirvikar

This tool ingests official government schemes datasets (CSV or JSON),
cleans and normalizes records, removes duplicates, validates official URLs,
and outputs standardized UTF-8 JSON for both the Spring Boot backend
and frontend data directories.
"""

import argparse
import csv
import json
import os
import sys
from pathlib import Path
from urllib.parse import urlparse

DEFAULT_INPUT_FILE = Path(__file__).parent / "health_schemes.csv"
PROJECT_ROOT = Path(__file__).resolve().parent.parent.parent
DEFAULT_FRONTEND_OUTPUT = PROJECT_ROOT / "frontend" / "data" / "government_schemes.json"
DEFAULT_BACKEND_OUTPUT = PROJECT_ROOT / "backend" / "src" / "main" / "resources" / "data" / "government_schemes.json"

DEFAULT_ELIGIBILITY_FALLBACK = (
    "Please check the official government website for current eligibility criteria."
)


def clean_text(value):
    """Normalize whitespace and strip leading/trailing spaces."""
    if value is None:
        return ""
    return " ".join(str(value).split()).strip()


def is_valid_url(url_str):
    """Verify URL is well-formed with http/https scheme."""
    if not url_str:
        return False
    try:
        parsed = urlparse(url_str.strip())
        return parsed.scheme in ("http", "https") and bool(parsed.netloc)
    except Exception:
        return False


def parse_csv_file(file_path):
    """Read CSV file with automatic column name mapping."""
    records = []
    with open(file_path, "r", encoding="utf-8-sig", newline="") as f:
        reader = csv.DictReader(f)
        for row in reader:
            records.append(row)
    return records


def parse_json_file(file_path):
    """Read JSON array of scheme objects."""
    with open(file_path, "r", encoding="utf-8") as f:
        data = json.load(f)
        if isinstance(data, list):
            return data
        elif isinstance(data, dict) and "data" in data and isinstance(data["data"], list):
            return data["data"]
        elif isinstance(data, dict) and "records" in data and isinstance(data["records"], list):
            return data["records"]
        else:
            raise ValueError("Unsupported JSON format: root must be a list or contain records/data list.")


def extract_field(row, keys, default=""):
    for key in keys:
        if key in row and row[key] is not None:
            cleaned = clean_text(row[key])
            if cleaned:
                return cleaned
    return default


def normalize_schemes(raw_records):
    """
    Normalizes records to the standard schema:
    [
      {
        "id": 1,
        "scheme_name": "...",
        "short_description": "...",
        "category": "...",
        "scheme_type": "...",
        "state": "...",
        "eligibility": "...",
        "official_url": "..."
      }
    ]
    """
    normalized = []
    seen_names = set()
    skipped_no_name = 0
    skipped_duplicates = 0

    name_keys = ["scheme_name", "Scheme Name", "name", "title", "SchemeName", "scheme_title"]
    desc_keys = ["short_description", "description", "Description", "short_desc", "summary", "details"]
    cat_keys = ["category", "Category", "sector", "health_category", "type_of_scheme"]
    type_keys = ["scheme_type", "Scheme Type", "level", "scheme_level", "type", "central_or_state"]
    state_keys = ["state", "State", "beneficiary_state", "location", "applicable_state", "state_name"]
    eligibility_keys = ["eligibility", "Eligibility", "eligibility_criteria", "who_can_apply", "target_beneficiary"]
    url_keys = ["official_url", "Official URL", "url", "link", "official_website", "source_url", "scheme_url"]

    for idx, row in enumerate(raw_records, start=1):
        if not isinstance(row, dict):
            continue

        name = extract_field(row, name_keys)
        if not name:
            skipped_no_name += 1
            continue

        # Deduplicate based on lowercase scheme name
        norm_key = name.lower()
        if norm_key in seen_names:
            skipped_duplicates += 1
            continue
        seen_names.add(norm_key)

        description = extract_field(row, desc_keys, default="Information about this government health scheme.")
        category = extract_field(row, cat_keys, default="Health & Wellness")
        scheme_type = extract_field(row, type_keys, default="Central Scheme")
        state = extract_field(row, state_keys, default="All India")
        eligibility = extract_field(row, eligibility_keys, default=DEFAULT_ELIGIBILITY_FALLBACK)
        official_url = extract_field(row, url_keys, default="")

        # Ensure safe official URL
        if official_url and not is_valid_url(official_url):
            print(f"[WARN] Invalid URL skipped for scheme '{name}': {official_url}")
            official_url = ""

        # Normalize state and scheme_type consistency
        if state.lower() in ("all india", "national", "central", "pan india", "entire country"):
            state = "All India"
        if scheme_type.lower() in ("central", "central scheme", "centrally sponsored", "central sector"):
            scheme_type = "Central Scheme"
        elif scheme_type.lower() in ("state", "state scheme", "state government"):
            scheme_type = "State Scheme"

        normalized.append({
            "id": len(normalized) + 1,
            "scheme_name": name,
            "short_description": description,
            "category": category,
            "scheme_type": scheme_type,
            "state": state,
            "eligibility": eligibility,
            "official_url": official_url
        })

    return normalized, skipped_no_name, skipped_duplicates


def main():
    parser = argparse.ArgumentParser(description="Convert & Normalize Government Health Schemes dataset.")
    parser.add_argument("--input", "-i", default=str(DEFAULT_INPUT_FILE), help="Input CSV or JSON file path.")
    parser.add_argument("--frontend-output", default=str(DEFAULT_FRONTEND_OUTPUT), help="Output frontend JSON path.")
    parser.add_argument("--backend-output", default=str(DEFAULT_BACKEND_OUTPUT), help="Output backend JSON path.")
    args = parser.parse_args()

    input_path = Path(args.input)
    if not input_path.is_file():
        print(f"[ERROR] Input file does not exist: {input_path}")
        sys.exit(1)

    print(f"Loading dataset from: {input_path}")
    if input_path.suffix.lower() == ".csv":
        raw_data = parse_csv_file(input_path)
    elif input_path.suffix.lower() == ".json":
        raw_data = parse_json_file(input_path)
    else:
        print(f"[ERROR] Unsupported file extension: {input_path.suffix}. Must be .csv or .json")
        sys.exit(1)

    print(f"Total raw rows read: {len(raw_data)}")
    normalized_schemes, skipped_no_name, skipped_duplicates = normalize_schemes(raw_data)

    frontend_out = Path(args.frontend_output)
    backend_out = Path(args.backend_output)

    frontend_out.parent.mkdir(parents=True, exist_ok=True)
    backend_out.parent.mkdir(parents=True, exist_ok=True)

    json_content = json.dumps(normalized_schemes, ensure_ascii=False, indent=2)

    with open(frontend_out, "w", encoding="utf-8") as f:
        f.write(json_content + "\n")
    print(f"Frontend JSON updated: {frontend_out}")

    with open(backend_out, "w", encoding="utf-8") as f:
        f.write(json_content + "\n")
    print(f"Backend JSON updated:  {backend_out}")

    print("\n================ IMPORT SUMMARY ================")
    print(f"Total imported schemes: {len(normalized_schemes)}")
    print(f"Skipped (no name):      {skipped_no_name}")
    print(f"Skipped (duplicates):   {skipped_duplicates}")
    print("================================================\n")


if __name__ == "__main__":
    main()