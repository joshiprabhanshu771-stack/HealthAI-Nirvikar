/**
 * HealthAI — Health Tips Interactive Client Engine
 * Handles Dynamic Category Filtering, Real-time Search, Today's Tip Cycling,
 * and Visual Guide Modal Rendering without Full Page Reloads.
 */

document.addEventListener('DOMContentLoaded', () => {
  // Application State
  let allTips = [];
  let currentCategory = 'All';
  let searchQuery = '';
  let todayTipIndex = 0;

  // DOM Elements
  const tipsContainer = document.getElementById('tipsGrid');
  const searchInput = document.getElementById('searchTipsInput');
  const searchClearBtn = document.getElementById('searchClearBtn');
  const filterButtons = document.querySelectorAll('.filter-btn');
  const todayBanner = document.getElementById('todayTipBanner');
  const nextTipBtn = document.getElementById('nextTipBtn');
  const modalOverlay = document.getElementById('tipModalOverlay');
  const modalCloseBtn = document.getElementById('modalCloseBtn');
  const modalBody = document.getElementById('modalBodyContent');
  const modalTitle = document.getElementById('modalTipTitle');
  const modalCategoryBadge = document.getElementById('modalCategoryBadge');

  // Initialize Data
  init();

  async function init() {
    await fetchTipsFromApi();

    setupEventListeners();
    renderTodayTip();
    renderTips();
  }

  async function fetchTipsFromApi() {
    try {
      const response = await fetch('/api/health-tips');
      if (response.ok) {
        allTips = await response.json();
      }
    } catch (error) {
      console.warn('[HealthTips] API fetch fallback to DOM elements:', error);
    }
  }

  function setupEventListeners() {
    // 1. Category Filter Clicks
    filterButtons.forEach(btn => {
      btn.addEventListener('click', (e) => {
        const selected = btn.getAttribute('data-category');
        if (selected) {
          filterButtons.forEach(b => b.classList.remove('active'));
          btn.classList.add('active');
          currentCategory = selected;
          renderTips();
        }
      });
    });

    // 2. Search Input Listener (Debounced)
    if (searchInput) {
      let debounceTimeout;
      searchInput.addEventListener('input', (e) => {
        clearTimeout(debounceTimeout);
        searchQuery = e.target.value.trim().toLowerCase();
        if (searchClearBtn) {
          searchClearBtn.style.display = searchQuery ? 'block' : 'none';
        }
        debounceTimeout = setTimeout(() => {
          renderTips();
        }, 150);
      });
    }

    // 3. Clear Search Button
    if (searchClearBtn) {
      searchClearBtn.addEventListener('click', () => {
        if (searchInput) searchInput.value = '';
        searchQuery = '';
        searchClearBtn.style.display = 'none';
        renderTips();
        if (searchInput) searchInput.focus();
      });
    }

    // 4. Next Tip Cycler
    if (nextTipBtn) {
      nextTipBtn.addEventListener('click', () => {
        if (allTips.length > 0) {
          todayTipIndex = (todayTipIndex + 1) % allTips.length;
          renderTodayTip();
        }
      });
    }

    // 5. Modal Close Listeners
    if (modalCloseBtn) {
      modalCloseBtn.addEventListener('click', closeModal);
    }

    if (modalOverlay) {
      modalOverlay.addEventListener('click', (e) => {
        if (e.target === modalOverlay) {
          closeModal();
        }
      });
    }

    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape' && modalOverlay && modalOverlay.classList.contains('active')) {
        closeModal();
      }
    });
  }

  // ==========================================
  // Render Today's Highlighted Tip
  // ==========================================
  function renderTodayTip() {
    if (!todayBanner || allTips.length === 0) return;

    const tip = allTips[todayTipIndex];
    const categoryClass = 'cat-' + (tip.category ? tip.category.replace(/\s+/g, '-') : 'Hydration');

    todayBanner.innerHTML = `
      <div class="today-tip-top">
        <div style="display: flex; align-items: center; gap: 0.75rem;">
          <span class="today-badge"><i class="fa-solid fa-sparkles"></i> Today's Health Tip</span>
          <span class="cat-badge ${categoryClass}">${escapeHtml(tip.category)}</span>
        </div>
        <button id="nextTipBtn" class="btn-next-tip" title="View another health tip">
          <i class="fa-solid fa-arrows-rotate"></i> Next Tip
        </button>
      </div>

      <h2 class="today-tip-title"><i class="fa-solid ${tip.icon || 'fa-lightbulb'} text-teal"></i> ${escapeHtml(tip.title)}</h2>
      <p class="today-tip-desc">${escapeHtml(tip.shortDescription || tip.description)}</p>

      <div class="today-tip-meta">
        <div class="today-meta-item">
          <strong><i class="fa-solid fa-circle-question"></i> Why It Matters</strong>
          <p>${escapeHtml(tip.whyItMatters || 'Supports optimal daily functioning and metabolic equilibrium.')}</p>
        </div>
        <div class="today-meta-item">
          <strong><i class="fa-solid fa-bullseye"></i> Practical Action</strong>
          <p>${escapeHtml(tip.actionableTip || 'Incorporate this simple habit into your daily routine.')}</p>
        </div>
      </div>

      <div style="margin-top: 1.5rem; display: flex; justify-content: space-between; align-items: center; flex-wrap: gap;">
        <button class="btn btn-primary" onclick="window.HealthTipsEngine.openModalById(${tip.id})">
          <i class="fa-solid fa-book-medical"></i> Read Full Guidance & Visual Guide
        </button>
        <span style="font-size: 0.85rem; color: #94a3b8;"><i class="fa-solid fa-shield-check"></i> Source: ${escapeHtml(tip.sourceName || 'WHO / Public Health')}</span>
      </div>
    `;

    // Re-bind Next Tip Button after innerHTML update
    const newNextBtn = todayBanner.querySelector('#nextTipBtn');
    if (newNextBtn) {
      newNextBtn.addEventListener('click', () => {
        todayTipIndex = (todayTipIndex + 1) % allTips.length;
        renderTodayTip();
      });
    }
  }

  // ==========================================
  // Render Health Tips Grid
  // ==========================================
  function renderTips() {
    if (!tipsContainer) return;

    // Filter by category and search
    const filtered = allTips.filter(tip => {
      const matchesCategory = (currentCategory === 'All' || tip.category.equalsIgnoreCase?.(currentCategory) || tip.category.toLowerCase() === currentCategory.toLowerCase());
      
      if (!matchesCategory) return false;

      if (!searchQuery) return true;

      const searchableText = [
        tip.title || '',
        tip.category || '',
        tip.shortDescription || '',
        tip.description || '',
        tip.keywords || '',
        tip.whyItMatters || '',
        tip.actionableTip || ''
      ].join(' ').toLowerCase();

      return searchableText.includes(searchQuery);
    });

    if (filtered.length === 0) {
      tipsContainer.innerHTML = `
        <div class="empty-state">
          <i class="fa-solid fa-magnifying-glass-chart"></i>
          <h3>No health tips found</h3>
          <p>We couldn't find any health tips matching "<strong>${escapeHtml(searchQuery)}</strong>" in the <em>${escapeHtml(currentCategory)}</em> category.</p>
          <button class="btn btn-secondary" onclick="window.HealthTipsEngine.resetSearch()">
            <i class="fa-solid fa-arrow-rotate-left"></i> Reset Search & Show All
          </button>
        </div>
      `;
      return;
    }

    tipsContainer.innerHTML = filtered.map(tip => {
      const categoryClass = 'cat-' + (tip.category ? tip.category.replace(/\s+/g, '-') : 'Hydration');
      const hasVisual = tip.visualType && tip.visualType !== 'none';
      const visualIcon = getVisualIcon(tip.visualType);

      return `
        <div class="tip-card" onclick="window.HealthTipsEngine.openModalById(${tip.id})">
          <div>
            <div class="tip-card-top">
              <div class="tip-icon-box ${categoryClass}">
                <i class="fa-solid ${tip.icon || 'fa-heart-pulse'}"></i>
              </div>
              <span class="cat-badge ${categoryClass}">${escapeHtml(tip.category)}</span>
            </div>
            <h3 class="tip-card-title">${escapeHtml(tip.title)}</h3>
            <p class="tip-card-desc">${escapeHtml(tip.shortDescription || tip.description)}</p>
          </div>

          <div class="tip-card-footer">
            <span class="tip-visual-indicator">
              ${hasVisual ? `<i class="fa-solid ${visualIcon} text-teal"></i> Includes Visual Guide` : '<i class="fa-solid fa-file-lines"></i> Evidence Guide'}
            </span>
            <button class="btn-read-more" aria-label="Read details for ${escapeHtml(tip.title)}">
              Read More <i class="fa-solid fa-arrow-right"></i>
            </button>
          </div>
        </div>
      `;
    }).join('');
  }

  function getVisualIcon(type) {
    switch (type) {
      case 'age_table': return 'fa-table';
      case 'guide_scale': return 'fa-sliders';
      case 'frequency': return 'fa-timeline';
      case 'checklist': return 'fa-list-check';
      case 'comparison': return 'fa-chart-pie';
      default: return 'fa-chart-simple';
    }
  }

  // ==========================================
  // Open and Populate Detail Modal
  // ==========================================
  function openModalById(id) {
    const tip = allTips.find(t => t.id === id);
    if (!tip || !modalOverlay) return;

    const categoryClass = 'cat-' + (tip.category ? tip.category.replace(/\s+/g, '-') : 'Hydration');

    if (modalTitle) {
      modalTitle.innerHTML = `<i class="fa-solid ${tip.icon || 'fa-heart-pulse'} text-teal"></i> ${escapeHtml(tip.title)}`;
    }

    if (modalCategoryBadge) {
      modalCategoryBadge.className = `cat-badge ${categoryClass}`;
      modalCategoryBadge.textContent = tip.category;
    }

    // Build Modal Body Content
    let visualHtml = renderVisualGuideHtml(tip);

    modalBody.innerHTML = `
      <!-- 1. Recommendation Overview -->
      <div class="modal-section">
        <h4 class="modal-section-title"><i class="fa-solid fa-circle-info"></i> General Health Guidance</h4>
        <p class="modal-text">${escapeHtml(tip.description || tip.shortDescription)}</p>
      </div>

      <!-- 2. Why It Matters -->
      <div class="modal-section">
        <h4 class="modal-section-title"><i class="fa-solid fa-brain"></i> Why It Matters</h4>
        <p class="modal-text">${escapeHtml(tip.whyItMatters)}</p>
      </div>

      <!-- 3. Visual Health Guide / Table / Scale -->
      ${visualHtml}

      <!-- 4. Practical Action -->
      <div class="modal-section">
        <h4 class="modal-section-title"><i class="fa-solid fa-circle-check"></i> Practical Everyday Actions</h4>
        <p class="modal-text">${escapeHtml(tip.actionableTip)}</p>
      </div>

      <!-- 5. Important Considerations -->
      <div class="modal-section">
        <h4 class="modal-section-title"><i class="fa-solid fa-triangle-exclamation text-amber"></i> Important Considerations</h4>
        <p class="modal-text">${escapeHtml(tip.importantConsiderations)}</p>
      </div>

      <!-- 6. Safety Alert & Reliable Source -->
      <div class="safety-alert">
        <strong><i class="fa-solid fa-shield-heart"></i> Evidence-Informed Medical Note</strong>
        <p>This recommendation provides general wellness education. It does not replace individualized clinical advice. Consult a healthcare provider if you have underlying medical conditions.</p>
      </div>

      <div class="source-box">
        <div class="source-info">
          <i class="fa-solid fa-building-columns text-teal"></i>
          <span>Verified Source: <strong>${escapeHtml(tip.sourceName || 'Public Health Authority')}</strong></span>
        </div>
        ${tip.sourceUrl ? `
          <a href="${escapeHtml(tip.sourceUrl)}" target="_blank" rel="noopener noreferrer" class="source-link">
            View Reference <i class="fa-solid fa-arrow-up-right-from-square font-small"></i>
          </a>
        ` : ''}
      </div>
    `;

    modalOverlay.classList.add('active');
    document.body.style.overflow = 'hidden';
  }

  // ==========================================
  // Render Dynamic Visual Guides (Tables, Charts, Checklists)
  // ==========================================
  function renderVisualGuideHtml(tip) {
    if (!tip.visualType || tip.visualType === 'none' || !tip.visualData) {
      return '';
    }

    try {
      let data = typeof tip.visualData === 'string' ? JSON.parse(tip.visualData) : tip.visualData;

      if (tip.visualType === 'age_table') {
        let headersHtml = (data.headers || []).map(h => `<th>${escapeHtml(h)}</th>`).join('');
        let rowsHtml = (data.rows || []).map(row => `
          <tr>
            ${row.map((cell, idx) => `<td>${idx === 0 ? `<strong>${escapeHtml(cell)}</strong>` : escapeHtml(cell)}</td>`).join('')}
          </tr>
        `).join('');

        let extraGuideHtml = '';
        if (data.extra_guide && Array.isArray(data.extra_guide.items)) {
          extraGuideHtml = `
            <div style="margin-top: 1.25rem;">
              <strong style="font-size: 0.9rem; color: var(--text-primary); display: block; margin-bottom: 0.5rem;">
                <i class="fa-solid fa-droplet text-blue"></i> ${escapeHtml(data.extra_guide.title)}
              </strong>
              <div class="color-scale-grid">
                ${data.extra_guide.items.map(item => `
                  <div class="color-scale-item">
                    <span class="color-swatch" style="background-color: ${item.color};"></span>
                    <div class="color-scale-info">
                      <strong>${escapeHtml(item.label)}</strong>
                      <small>${escapeHtml(item.status)}</small>
                    </div>
                  </div>
                `).join('')}
              </div>
            </div>
          `;
        }

        return `
          <div class="visual-guide-box">
            <div class="visual-guide-title">
              <i class="fa-solid fa-table text-teal"></i> ${escapeHtml(data.title || 'Visual Guide')}
            </div>
            <div class="guide-table-responsive">
              <table class="guide-table">
                <thead><tr>${headersHtml}</tr></thead>
                <tbody>${rowsHtml}</tbody>
              </table>
            </div>
            ${data.note ? `<div class="guide-table-note"><i class="fa-solid fa-circle-info"></i> ${escapeHtml(data.note)}</div>` : ''}
            ${extraGuideHtml}
          </div>
        `;
      }

      if (tip.visualType === 'frequency' || tip.visualType === 'timeline') {
        let stepsHtml = (data.steps || []).map((step, idx) => `
          <div class="timeline-step">
            <span class="step-badge">${escapeHtml(step.timing || `Step ${idx+1}`)}</span>
            <p style="margin: 0; font-size: 0.95rem; color: var(--text-secondary); line-height: 1.5;">
              ${escapeHtml(step.guidance || step.desc)}
            </p>
          </div>
        `).join('');

        return `
          <div class="visual-guide-box">
            <div class="visual-guide-title">
              <i class="fa-solid fa-timeline text-teal"></i> ${escapeHtml(data.title || 'Guidance Protocol')}
            </div>
            <div class="guide-timeline">${stepsHtml}</div>
          </div>
        `;
      }

      if (tip.visualType === 'guide_scale') {
        let stepsHtml = (data.steps || []).map(s => `
          <div class="color-scale-item" style="flex-direction: column; align-items: flex-start; gap: 0.35rem;">
            <div style="display: flex; justify-content: space-between; width: 100%; align-items: center;">
              <strong style="color: var(--primary-teal);">${escapeHtml(s.phase)}</strong>
              <span class="step-badge">${escapeHtml(s.duration)}</span>
            </div>
            <p style="font-size: 0.85rem; color: var(--text-secondary); margin: 0;">${escapeHtml(s.desc)}</p>
          </div>
        `).join('');

        return `
          <div class="visual-guide-box">
            <div class="visual-guide-title">
              <i class="fa-solid fa-sliders text-teal"></i> ${escapeHtml(data.title || 'Technique Scale')}
            </div>
            <div class="grid grid-2" style="gap: 0.75rem;">${stepsHtml}</div>
          </div>
        `;
      }

      if (tip.visualType === 'comparison') {
        let sectionsHtml = (data.sections || []).map(sec => `
          <div class="plate-item">
            <span class="plate-portion">${escapeHtml(sec.portion)}</span>
            <div class="plate-name">${escapeHtml(sec.name)}</div>
            <p class="plate-desc">${escapeHtml(sec.description)}</p>
          </div>
        `).join('');

        return `
          <div class="visual-guide-box">
            <div class="visual-guide-title">
              <i class="fa-solid fa-chart-pie text-teal"></i> ${escapeHtml(data.title || 'Visual Model')}
            </div>
            <div class="plate-grid">${sectionsHtml}</div>
          </div>
        `;
      }

      if (tip.visualType === 'checklist') {
        let itemsHtml = (data.items || []).map(item => `
          <li>
            <i class="fa-solid fa-circle-check"></i>
            <span>${escapeHtml(item)}</span>
          </li>
        `).join('');

        return `
          <div class="visual-guide-box">
            <div class="visual-guide-title">
              <i class="fa-solid fa-list-check text-teal"></i> ${escapeHtml(data.title || 'Guidance Checklist')}
            </div>
            <ul class="guide-checklist">${itemsHtml}</ul>
          </div>
        `;
      }

    } catch (e) {
      console.error('[HealthTips] Error parsing visual_data JSON:', e);
    }

    return '';
  }

  function closeModal() {
    if (modalOverlay) {
      modalOverlay.classList.remove('active');
      document.body.style.overflow = '';
    }
  }

  function resetSearch() {
    if (searchInput) searchInput.value = '';
    searchQuery = '';
    currentCategory = 'All';
    if (searchClearBtn) searchClearBtn.style.display = 'none';
    filterButtons.forEach(b => {
      b.classList.toggle('active', b.getAttribute('data-category') === 'All');
    });
    renderTips();
  }

  function escapeHtml(str) {
    if (!str) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#039;');
  }

  // Expose methods globally for inline onclick handlers
  window.HealthTipsEngine = {
    openModalById,
    closeModal,
    resetSearch
  };
});
