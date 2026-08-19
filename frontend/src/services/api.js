const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

export async function getHealthTips() {
  const response = await fetch(`${API_BASE_URL}/api/health-tips`);
  if (!response.ok) {
    throw new Error(`Health tips request failed: ${response.status}`);
  }
  return response.json();
}