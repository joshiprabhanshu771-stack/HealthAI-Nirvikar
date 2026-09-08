import { getDisease } from './api.js';

function escapeHtml(str = '') {
  return String(str).replace(/[&<>'"]/g, match => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    "'": '&#39;',
    '"': '&quot;'
  }[match]));
}

function cleanListItems(items) {
  if (!Array.isArray(items) || items.length === 0) {
    return [];
  }
  return items.flatMap(item => {
    if (!item) return [];
    return String(item)
      .split(/\r?\n/)
      .map(line => line.replace(/^[\s•\-*–—]+/, '').replace(/^\d+[.)]\s*/, '').trim())
      .filter(Boolean);
  });
}

function renderShell() {
  const app = document.querySelector('#app');
  app.innerHTML = `
    <div class="app-container">
      <!-- Navbar -->
      <header class="navbar-header">
        <nav class="navbar container">
          <a href="user_dashboard.html" class="logo">
            <span class="logo-icon"><i class="fa-solid fa-heart-pulse"></i></span>
            <span class="logo-text">Health<span class="logo-accent">AI</span></span>
          </a>

          <input type="checkbox" id="nav-toggle" class="nav-toggle">
          <label for="nav-toggle" class="nav-toggle-label" aria-label="Toggle Navigation Menu">
            <i class="fa-solid fa-bars icon-open"></i>
            <i class="fa-solid fa-xmark icon-close"></i>
          </label>

          <ul class="nav-menu">
            <li><a href="user_dashboard.html" class="nav-link">Home</a></li>
            <li><a href="disease.html" class="nav-link active">Diseases</a></li>
            <li><a href="health_and_wellness.html" class="nav-link">Health &amp; Wellness</a></li>
            <li><a href="health_tips.html" class="nav-link">Health Tips</a></li>
          </ul>

          <div class="nav-actions">
            <a href="login.html" class="btn btn-tertiary">Log In</a>
            <a href="signup.html" class="btn btn-primary">Sign Up</a>
          </div>
        </nav>
      </header>

      <!-- Main Detail View -->
      <main class="main-content">
        <div class="detail-container">
          <!-- Back Navigation Link -->
          <div class="detail-nav">
            <a href="disease.html" class="btn-back">
              <i class="fa-solid fa-arrow-left"></i> Back to Disease Library
            </a>
          </div>

          <!-- Live Status Area -->
          <div id="status" class="status" aria-live="polite">
            <div class="loading">
              <i class="fa-solid fa-circle-notch fa-spin"></i>
              <span>Loading disease details from medical database...</span>
            </div>
          </div>

          <!-- Dynamic Disease Record Container -->
          <div id="disease-detail"></div>
        </div>
      </main>

      <!-- Footer -->
      <footer class="site-footer">
        <div class="container">
          <div class="footer-bottom">
            <p>&copy; 2026 HealthAI. All Rights Reserved.</p>
            <div class="footer-links">
              <a href="disease.html">Disease Library</a>
              <a href="health_and_wellness.html">Wellness Hub</a>
              <a href="health_tips.html">Daily Tips</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  `;
}

function createSectionCard(title, iconClass, items) {
  const values = cleanListItems(items);
  if (values.length === 0) {
    return ''; // Hide completely
  }

  return `
    <section class="detail-card">
      <h2>
        <i class="${iconClass}"></i> ${escapeHtml(title)}
      </h2>
      <ul>
        ${values.map(item => `<li>${escapeHtml(item)}</li>`).join('')}
      </ul>
    </section>
  `;
}

function renderDiseaseDetail(disease) {
  const container = document.querySelector('#disease-detail');
  const validSources = (disease.sources || []).filter(s => s && s.url);

  const symptomsCard = createSectionCard('Symptoms', 'fa-solid fa-stethoscope', disease.symptoms);
  const causesCard = createSectionCard('Causes', 'fa-solid fa-virus', disease.causes);
  const riskFactorsCard = createSectionCard('Risk Factors', 'fa-solid fa-chart-line', disease.riskFactors);
  const diagnosisCard = createSectionCard('Diagnosis & Tests', 'fa-solid fa-microscope', disease.diagnosis);
  const treatmentsCard = createSectionCard('Treatments & Therapies', 'fa-solid fa-prescription-bottle-medical', disease.treatments);
  const preventionCard = createSectionCard('Prevention', 'fa-solid fa-shield-heart', disease.prevention);
  const emergencySignsCard = createSectionCard('Emergency Warning Signs', 'fa-solid fa-triangle-exclamation', disease.emergencySigns);

  const hasAnySection = symptomsCard || causesCard || riskFactorsCard || diagnosisCard || treatmentsCard || preventionCard || emergencySignsCard;

  container.innerHTML = `
    <!-- Hero Header -->
    <header class="detail-hero">
      <div class="detail-kicker">
        ${disease.category ? `<span class="badge teal-badge"><i class="fa-solid fa-tag"></i> ${escapeHtml(disease.category.name)}</span>` : ''}
        <span><i class="fa-solid fa-certificate text-teal"></i> Official MedlinePlus Resource</span>
      </div>
      <h1>${escapeHtml(disease.name)}</h1>
      ${disease.overview ? `<p>${escapeHtml(disease.overview)}</p>` : ''}
    </header>

    <!-- Clinical Details Grid -->
    ${hasAnySection ? `
      <div class="detail-grid">
        ${symptomsCard}
        ${causesCard}
        ${riskFactorsCard}
        ${diagnosisCard}
        ${treatmentsCard}
        ${preventionCard}
        ${emergencySignsCard}
      </div>
    ` : `
      <div class="empty-state" style="text-align: center; padding: 2rem; background: var(--bg-subtle); border-radius: var(--radius-md);">
        <i class="fa-solid fa-circle-info" style="font-size: 2rem; color: var(--primary-teal); margin-bottom: 0.5rem;"></i>
        <p>Specific sub-sections for this condition are currently being indexed. Please consult the verified medical sources below.</p>
      </div>
    `}

    <!-- Medical Sources Section -->
    ${validSources.length > 0 ? `
      <section class="source-panel">
        <h2>
          <i class="fa-solid fa-building-columns"></i> Verified Medical Sources
        </h2>
        <div class="source-list">
          ${validSources.map(source => `
            <div class="source-row">
              <div class="source-info">
                <strong>${escapeHtml(source.name || 'MedlinePlus Medical Reference')}</strong>
                ${source.description ? `<p>${escapeHtml(source.description)}</p>` : ''}
              </div>
              <a class="btn btn-tertiary" href="${escapeHtml(source.url)}" target="_blank" rel="noopener noreferrer">
                Visit Official Source <i class="fa-solid fa-arrow-up-right-from-square"></i>
              </a>
            </div>
          `).join('')}
        </div>
      </section>
    ` : ''}

    <!-- Disclaimer Banner -->
    <div class="disclaimer-banner" style="margin-top: 2.5rem;">
      <div class="disclaimer-left">
        <i class="fa-solid fa-circle-info"></i>
        <span>This medical summary is for reference and education only. If you are experiencing symptoms or require a diagnosis, consult a qualified healthcare provider.</span>
      </div>
    </div>
  `;

  document.title = `${disease.name} - Disease Details - HealthAI`;
}

// Initialize Page Shell
renderShell();

// Parse disease ID from URL (?id=...)
const params = new URLSearchParams(window.location.search);
const diseaseId = params.get('id');

const statusEl = document.querySelector('#status');

if (!diseaseId) {
  statusEl.innerHTML = `
    <div class="error-state">
      <i class="fa-solid fa-circle-question"></i>
      <h2>No Disease Specified</h2>
      <p>Please select a disease from the directory to view its clinical details.</p>
      <a class="btn btn-primary" href="disease.html">
        <i class="fa-solid fa-arrow-left"></i> Go to Disease Library
      </a>
    </div>
  `;
} else {
  getDisease(diseaseId)
    .then(disease => {
      statusEl.textContent = '';
      renderDiseaseDetail(disease);
    })
    .catch(error => {
      statusEl.innerHTML = `
        <div class="error-state">
          <i class="fa-solid fa-triangle-exclamation"></i>
          <h2>Disease Record Not Found</h2>
          <p>${escapeHtml(error.message)}</p>
          <a class="btn btn-primary" href="disease.html">
            <i class="fa-solid fa-arrow-left"></i> Return to Disease Library
          </a>
        </div>
      `;
    });
}
