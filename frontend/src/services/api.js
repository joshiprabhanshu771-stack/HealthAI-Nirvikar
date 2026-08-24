const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

export async function getHealthTips() {
  const response = await fetch(`${API_BASE_URL}/api/health-tips`);
  if (!response.ok) {
    throw new Error(`Health tips request failed: ${response.status}`);
  }
  return response.json();
}

export async function getPregnancyTrimesters() {
  const response = await fetch(`${API_BASE_URL}/api/pregnancy-care/trimesters`);
  if (!response.ok) {
    throw new Error(`Pregnancy trimesters request failed: ${response.status}`);
  }
  return response.json();
}

export async function getPregnancyNutrition(category = '') {
  const url = category ? `${API_BASE_URL}/api/pregnancy-care/nutrition?category=${encodeURIComponent(category)}` : `${API_BASE_URL}/api/pregnancy-care/nutrition`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Pregnancy nutrition request failed: ${response.status}`);
  }
  return response.json();
}

export async function getPregnancyWarningSigns() {
  const response = await fetch(`${API_BASE_URL}/api/pregnancy-care/warning-signs`);
  if (!response.ok) {
    throw new Error(`Pregnancy warning signs request failed: ${response.status}`);
  }
  return response.json();
}

export async function calculatePregnancyStage(lmpDate) {
  const response = await fetch(`${API_BASE_URL}/api/pregnancy-care/calculate?lmp=${encodeURIComponent(lmpDate)}`);
  if (!response.ok) {
    throw new Error(`Pregnancy calculate request failed: ${response.status}`);
  }
  return response.json();
}