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

export async function getChildMilestones() {
  const response = await fetch(`${API_BASE_URL}/api/child-health/milestones`);
  if (!response.ok) {
    throw new Error(`Child milestones request failed: ${response.status}`);
  }
  return response.json();
}

export async function getChildVaccines(age = '') {
  const url = age ? `${API_BASE_URL}/api/child-health/vaccines?age=${encodeURIComponent(age)}` : `${API_BASE_URL}/api/child-health/vaccines`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Child vaccines request failed: ${response.status}`);
  }
  return response.json();
}

export async function getChildIllnessGuides(category = '') {
  const url = category ? `${API_BASE_URL}/api/child-health/illnesses?category=${encodeURIComponent(category)}` : `${API_BASE_URL}/api/child-health/illnesses`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Child illness guides request failed: ${response.status}`);
  }
  return response.json();
}

export async function getBloodDonors(bloodGroup = '', city = '') {
  let url = `${API_BASE_URL}/api/blood-donation/donors`;
  const params = [];
  if (bloodGroup && bloodGroup !== 'All') params.push(`bloodGroup=${encodeURIComponent(bloodGroup)}`);
  if (city) params.push(`city=${encodeURIComponent(city)}`);
  if (params.length > 0) url += `?${params.join('&')}`;

  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Blood donors request failed: ${response.status}`);
  }
  return response.json();
}

export async function registerBloodDonor(donorData) {
  const response = await fetch(`${API_BASE_URL}/api/blood-donation/donors`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(donorData)
  });
  if (!response.ok) {
    throw new Error(`Donor registration failed: ${response.status}`);
  }
  return response.json();
}

export async function getBloodRequests(bloodGroup = '', status = '') {
  let url = `${API_BASE_URL}/api/blood-donation/requests`;
  const params = [];
  if (bloodGroup && bloodGroup !== 'All') params.push(`bloodGroup=${encodeURIComponent(bloodGroup)}`);
  if (status && status !== 'All') params.push(`status=${encodeURIComponent(status)}`);
  if (params.length > 0) url += `?${params.join('&')}`;

  const response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Blood requests request failed: ${response.status}`);
  }
  return response.json();
}

export async function createBloodRequest(reqData) {
  const response = await fetch(`${API_BASE_URL}/api/blood-donation/requests`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(reqData)
  });
  if (!response.ok) {
    throw new Error(`Blood request creation failed: ${response.status}`);
  }
  return response.json();
}

export async function getBloodCompatibility(bloodGroup = 'O+') {
  const response = await fetch(`${API_BASE_URL}/api/blood-donation/compatibility?bloodGroup=${encodeURIComponent(bloodGroup)}`);
  if (!response.ok) {
    throw new Error(`Compatibility request failed: ${response.status}`);
  }
  return response.json();
}