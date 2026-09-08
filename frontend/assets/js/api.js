// Resolve backend API URL dynamically:
// 1. Explicit window override if configured
// 2. Same origin if running on backend server (port 8080)
// 3. Fallback to http://localhost:8080 for standalone static servers (Live Server, serve, etc.)
const API_BASE = (typeof window !== 'undefined' && window.HEALTHAI_API_BASE) || (
  typeof window !== 'undefined' && window.location.port === '8080'
    ? ''
    : 'http://localhost:8080'
);

async function fetchJson(path) {
  const url = `${API_BASE}${path}`;
  const response = await fetch(url);
  if (!response.ok) {
    let message = `Request failed with status ${response.status}`;
    try {
      const body = await response.json();
      if (body.message) message = body.message;
    } catch {
      // Keep status code message if body parsing fails
    }
    throw new Error(message);
  }
  return response.json();
}

/**
 * Fetch all disease categories.
 * GET /api/categories
 */
export function getCategories() {
  return fetchJson('/api/categories');
}

/**
 * Fetch a single category by its ID.
 * GET /api/categories/{categoryId}
 */
export function getCategory(categoryId) {
  return fetchJson(`/api/categories/${encodeURIComponent(categoryId)}`);
}

/**
 * Fetch paginated disease list.
 * GET /api/diseases?page={page}&size={size}
 */
export function getDiseases(page = 0, size = 12) {
  return fetchJson(`/api/diseases?page=${page}&size=${size}`);
}

/**
 * Search diseases by name.
 * GET /api/diseases/search?name={query}
 */
export function searchDiseases(name) {
  return fetchJson(`/api/diseases/search?name=${encodeURIComponent(name.trim())}`);
}

/**
 * Fetch diseases by category ID.
 * GET /api/diseases/category/{categoryId}
 */
export function getDiseasesByCategory(categoryId) {
  return fetchJson(`/api/diseases/category/${encodeURIComponent(categoryId)}`);
}

/**
 * Fetch diseases for a category using the Category API.
 * GET /api/categories/{categoryId}/diseases
 */
export function getCategoryDiseases(categoryId) {
  return fetchJson(`/api/categories/${encodeURIComponent(categoryId)}/diseases`);
}

/**
 * Fetch complete disease detail by ID.
 * GET /api/diseases/{id}
 */
export function getDisease(id) {
  return fetchJson(`/api/diseases/${encodeURIComponent(id)}`);
}

/**
 * Fetch all health tips.
 * GET /api/health-tips
 */
export function getHealthTips() {
  return fetchJson('/api/health-tips');
}
