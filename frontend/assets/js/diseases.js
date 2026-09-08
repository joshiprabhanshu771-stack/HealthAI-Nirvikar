import { getCategories, getDiseases, getDiseasesByCategory, searchDiseases } from './api.js';

const state = {
  page: 0,
  pageSize: 12,
  categoryId: null,
  categoryName: '',
  query: '',
  totalPages: 0,
  totalElements: 0,
  requestId: 0
};

const categoryIconMap = [
  ['respiratory', 'teal', 'fa-solid fa-lungs'],
  ['cardiovascular', 'pink', 'fa-solid fa-heart-pulse'],
  ['heart', 'pink', 'fa-solid fa-heart-pulse'],
  ['blood', 'pink', 'fa-solid fa-droplet'],
  ['neurological', 'indigo', 'fa-solid fa-brain'],
  ['neuro', 'indigo', 'fa-solid fa-brain'],
  ['infectious', 'green', 'fa-solid fa-virus'],
  ['digestive', 'orange', 'fa-solid fa-cookie-bite'],
  ['metabolic', 'amber', 'fa-solid fa-dna'],
  ['skin', 'blue', 'fa-solid fa-hand-dots'],
  ['kidney', 'blue', 'fa-solid fa-filter'],
  ['eye', 'blue', 'fa-solid fa-eye'],
  ['ent', 'teal', 'fa-solid fa-head-side-cough'],
  ['women', 'pink', 'fa-solid fa-venus'],
  ['child', 'green', 'fa-solid fa-child'],
  ['bone', 'amber', 'fa-solid fa-bone'],
  ['mental', 'indigo', 'fa-solid fa-head-side-virus']
];

function escapeHtml(str = '') {
  return String(str).replace(/[&<>'"]/g, match => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    "'": '&#39;',
    '"': '&quot;'
  }[match]));
}

function getCategoryStyle(categoryName) {
  const normalized = (categoryName || '').toLowerCase();
  const match = categoryIconMap.find(([term]) => normalized.includes(term));
  if (match) {
    return [match[1], match[2]];
  }
  return ['blue', 'fa-solid fa-notes-medical'];
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

      <!-- Main Content -->
      <main class="main-content">
        <!-- Hero Section -->
        <section class="hero-banner">
          <div class="hero-text">
            <div class="badge badge-teal" style="margin-bottom: 0.75rem;">
              <i class="fa-solid fa-circle-check"></i> Evidence-Informed Medical Directory
            </div>
            <h1>Disease Information Library</h1>
            <p>
              Explore reliable clinical knowledge on symptoms, causes, diagnosis, treatments, and prevention sourced from official medical references.
            </p>

            <!-- Search Bar -->
            <div class="search-box">
              <i class="fa-solid fa-magnifying-glass search-icon"></i>
              <input id="disease-search" type="search" placeholder="Search diseases by name (e.g. Asthma, Diabetes)..." aria-label="Search diseases by name">
              <button id="clear-search" class="clear-search" type="button" hidden aria-label="Clear search">
                <i class="fa-solid fa-xmark"></i>
              </button>
            </div>
          </div>
          <div class="hero-illustration" aria-hidden="true">
            <i class="fa-solid fa-book-medical"></i>
          </div>
        </section>

        <!-- Categories Bar -->
        <section id="category-bar" class="categories-bar" aria-label="Disease Categories">
          <div class="chip skeleton-chip">Loading categories...</div>
        </section>

        <!-- Diseases List Section -->
        <section class="disease-section">
          <div class="section-header">
            <h2 id="results-title">
              <i class="fa-solid fa-book-medical text-teal"></i> All Available Diseases
            </h2>
            <span id="results-count" class="results-count"></span>
          </div>

          <!-- Status Message (Loading / Error) -->
          <div id="status" class="status" aria-live="polite"></div>

          <!-- Disease Cards Grid -->
          <div id="disease-grid" class="disease-grid"></div>

          <!-- Pagination -->
          <div id="pagination" class="pagination" aria-label="Disease pagination" hidden></div>
        </section>

        <!-- Medical Disclaimer -->
        <div class="disclaimer-banner">
          <div class="disclaimer-left">
            <i class="fa-solid fa-circle-info"></i>
            <span>Medical information is provided for educational purposes only and is not a substitute for professional medical advice, diagnosis, or treatment.</span>
          </div>
          <div class="disclaimer-right">
            Source: <strong>Official MedlinePlus / NIH</strong>
          </div>
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

function renderCategories(categories) {
  const bar = document.querySelector('#category-bar');
  if (!categories || !categories.length) {
    bar.innerHTML = '<span class="status">No categories found.</span>';
    return;
  }

  bar.innerHTML = `
    <button class="chip active" data-category="all">
      <i class="fa-solid fa-layer-group"></i> All Categories
    </button>
    ${categories.map(cat => {
      const [, icon] = getCategoryStyle(cat.name);
      return `
        <button class="chip" data-category="${cat.id}" data-name="${escapeHtml(cat.name)}">
          <i class="${icon}"></i> ${escapeHtml(cat.name)}
        </button>
      `;
    }).join('')}
  `;

  bar.addEventListener('click', event => {
    const button = event.target.closest('[data-category]');
    if (!button) return;

    const catId = button.dataset.category;
    if (catId === 'all') {
      state.categoryId = null;
      state.categoryName = '';
    } else {
      state.categoryId = Number(catId);
      state.categoryName = button.dataset.name || '';
    }

    state.query = '';
    state.page = 0;

    const searchInput = document.querySelector('#disease-search');
    const clearBtn = document.querySelector('#clear-search');
    if (searchInput) searchInput.value = '';
    if (clearBtn) clearBtn.hidden = true;

    bar.querySelectorAll('.chip').forEach(chip => {
      chip.classList.toggle('active', chip === button);
    });

    loadDiseases();
  });
}

function renderDiseases(diseases) {
  const grid = document.querySelector('#disease-grid');
  if (!diseases || diseases.length === 0) {
    grid.innerHTML = `
      <div class="empty-state" style="grid-column: 1 / -1; text-align: center; padding: 3rem 1rem;">
        <i class="fa-solid fa-folder-open" style="font-size: 2.5rem; color: var(--text-muted); margin-bottom: 1rem;"></i>
        <h3>No Diseases Found</h3>
        <p style="color: var(--text-secondary); margin-bottom: 1.5rem;">
          ${state.query
            ? `No conditions matched "${escapeHtml(state.query)}". Check your spelling or try another keyword.`
            : 'No disease records are currently available for this category.'}
        </p>
        <button class="btn btn-secondary" id="reset-filter">
          <i class="fa-solid fa-arrow-rotate-left"></i> View All Diseases
        </button>
      </div>
    `;

    const resetBtn = grid.querySelector('#reset-filter');
    if (resetBtn) {
      resetBtn.addEventListener('click', () => {
        state.query = '';
        state.categoryId = null;
        state.categoryName = '';
        state.page = 0;
        const searchInput = document.querySelector('#disease-search');
        const clearBtn = document.querySelector('#clear-search');
        if (searchInput) searchInput.value = '';
        if (clearBtn) clearBtn.hidden = true;
        document.querySelectorAll('#category-bar .chip').forEach(chip => {
          chip.classList.toggle('active', chip.dataset.category === 'all');
        });
        loadDiseases();
      });
    }
    return;
  }

  grid.innerHTML = diseases.map(disease => {
    const [color, icon] = getCategoryStyle(disease.category?.name);
    const overview = disease.overview
      ? (disease.overview.length > 150 ? `${disease.overview.slice(0, 150)}...` : disease.overview)
      : 'Open this record to view clinical information on symptoms, causes, diagnosis, and treatments.';

    return `
      <article class="disease-card">
        <div class="card-top">
          <div class="card-icon ${color}">
            <i class="${icon}"></i>
          </div>
          ${disease.category ? `<span class="badge ${color}-badge">${escapeHtml(disease.category.name)}</span>` : ''}
        </div>
        <h3>${escapeHtml(disease.name)}</h3>
        <p>${escapeHtml(overview)}</p>
        <a class="view-details" href="disease-detail.html?id=${encodeURIComponent(disease.id)}">
          View Details <i class="fa-solid fa-arrow-right"></i>
        </a>
      </article>
    `;
  }).join('');
}

function renderPagination() {
  const pagination = document.querySelector('#pagination');
  const isPaged = !state.query && state.categoryId === null && state.totalPages > 1;
  pagination.hidden = !isPaged;
  if (!isPaged) return;

  pagination.innerHTML = `
    <button class="btn btn-tertiary" data-page="prev" ${state.page === 0 ? 'disabled' : ''} aria-label="Previous Page">
      <i class="fa-solid fa-chevron-left"></i> Previous
    </button>
    <span class="page-indicator">Page <strong>${state.page + 1}</strong> of <strong>${state.totalPages}</strong></span>
    <button class="btn btn-tertiary" data-page="next" ${state.page >= state.totalPages - 1 ? 'disabled' : ''} aria-label="Next Page">
      Next <i class="fa-solid fa-chevron-right"></i>
    </button>
  `;
}

async function loadDiseases() {
  const currentRequest = ++state.requestId;
  const status = document.querySelector('#status');
  const grid = document.querySelector('#disease-grid');
  const countEl = document.querySelector('#results-count');
  const titleEl = document.querySelector('#results-title');

  status.innerHTML = `
    <div class="loading">
      <i class="fa-solid fa-circle-notch fa-spin"></i>
      <span>Loading disease records...</span>
    </div>
  `;

  try {
    let data;
    if (state.query) {
      data = await searchDiseases(state.query);
    } else if (state.categoryId !== null) {
      data = await getDiseasesByCategory(state.categoryId);
    } else {
      data = await getDiseases(state.page, state.pageSize);
    }

    if (currentRequest !== state.requestId) return;

    let diseases = [];
    if (Array.isArray(data)) {
      diseases = data;
      state.totalPages = 1;
      state.totalElements = diseases.length;
    } else if (data && Array.isArray(data.content)) {
      diseases = data.content;
      state.totalPages = data.totalPages || 1;
      state.totalElements = data.totalElements || diseases.length;
    }

    countEl.textContent = `${state.totalElements} condition${state.totalElements === 1 ? '' : 's'}`;

    if (state.query) {
      titleEl.innerHTML = `<i class="fa-solid fa-magnifying-glass text-teal"></i> Results for &quot;${escapeHtml(state.query)}&quot;`;
    } else if (state.categoryId !== null) {
      titleEl.innerHTML = `<i class="fa-solid fa-folder-tree text-teal"></i> ${escapeHtml(state.categoryName || 'Category')} Diseases`;
    } else {
      titleEl.innerHTML = `<i class="fa-solid fa-book-medical text-teal"></i> All Available Diseases`;
    }

    status.textContent = '';
    renderDiseases(diseases);
    renderPagination();
  } catch (error) {
    if (currentRequest !== state.requestId) return;
    status.innerHTML = `
      <div class="error-state">
        <i class="fa-solid fa-triangle-exclamation"></i>
        <h3>Unable to Load Diseases</h3>
        <p>${escapeHtml(error.message)}</p>
        <button class="btn btn-primary" id="retry-btn">
          <i class="fa-solid fa-arrow-rotate-right"></i> Try Again
        </button>
      </div>
    `;
    grid.innerHTML = '';
    document.querySelector('#pagination').hidden = true;
    const retryBtn = document.querySelector('#retry-btn');
    if (retryBtn) retryBtn.addEventListener('click', loadDiseases);
  }
}

// Initialize Application
renderShell();

// Load Categories
getCategories()
  .then(renderCategories)
  .catch(err => {
    const bar = document.querySelector('#category-bar');
    if (bar) bar.innerHTML = `<span class="status" style="font-size: 0.9rem;">Categories unavailable (${escapeHtml(err.message)})</span>`;
  });

// Setup Search Input with 300ms Debounce
const searchInput = document.querySelector('#disease-search');
const clearSearchBtn = document.querySelector('#clear-search');

let searchDebounceTimer;
searchInput.addEventListener('input', event => {
  clearTimeout(searchDebounceTimer);
  const val = event.target.value;
  clearSearchBtn.hidden = !val;

  searchDebounceTimer = setTimeout(() => {
    state.query = val.trim();
    state.categoryId = null;
    state.categoryName = '';
    state.page = 0;

    // Reset category chip selection
    document.querySelectorAll('#category-bar .chip').forEach(chip => {
      chip.classList.toggle('active', chip.dataset.category === 'all');
    });

    loadDiseases();
  }, 300);
});

clearSearchBtn.addEventListener('click', () => {
  searchInput.value = '';
  clearSearchBtn.hidden = true;
  state.query = '';
  state.page = 0;
  loadDiseases();
});

// Setup Pagination Listener
document.querySelector('#pagination').addEventListener('click', event => {
  const btn = event.target.closest('[data-page]');
  if (!btn || btn.disabled) return;

  const direction = btn.dataset.page;
  if (direction === 'next' && state.page < state.totalPages - 1) {
    state.page += 1;
    loadDiseases();
    window.scrollTo({ top: 320, behavior: 'smooth' });
  } else if (direction === 'prev' && state.page > 0) {
    state.page -= 1;
    loadDiseases();
    window.scrollTo({ top: 320, behavior: 'smooth' });
  }
});

// Initial load
loadDiseases();
