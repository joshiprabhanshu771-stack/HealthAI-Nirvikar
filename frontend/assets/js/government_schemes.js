/**
 * HealthAI - Government Health Schemes Module
 * Robust Vanilla JavaScript Implementation
 */

document.addEventListener("DOMContentLoaded", () => {
    // =====================================================
    // DOM ELEMENTS
    // =====================================================
    const schemeGrid = document.getElementById("schemeGrid");
    const searchInput = document.querySelector(".search-input input");
    const searchButton = document.querySelector(".search-btn");

    const categoryFilter = document.getElementById("categorySelect");
    const schemeTypeFilter = document.getElementById("schemeTypeSelect");
    const stateFilter = document.getElementById("stateSelect");

    const categoryButtons = document.querySelectorAll(".category");
    const viewAllSchemes = document.getElementById("viewAllSchemes");
    const schemeCounter = document.getElementById("schemeCounter");
    const loadMoreContainer = document.getElementById("loadMoreContainer");
    const loadMoreBtn = document.getElementById("loadMoreBtn");

    const schemeModal = document.getElementById("schemeModal");
    const schemeModalBody = document.getElementById("schemeModalBody");
    const closeSchemeModal = document.getElementById("closeSchemeModal");

    // =====================================================
    // STATE & CONFIG
    // =====================================================
    const BATCH_SIZE = 20;
    let allSchemes = [];
    let currentFilteredSchemes = [];
    let displayedCount = BATCH_SIZE;
    let activeCategory = "All Schemes";

    // Category emoji mapping helper
    const categoryIcons = {
        "Health Insurance": "🛡",
        "Women & Maternal Health": "👩",
        "Child Health": "👶",
        "Senior Citizens": "👴",
        "Treatment & Medical Assistance": "✚",
        "Immunization": "💉",
        "Disease Control": "🦠",
        "Mental Health": "🧠",
        "Disability Health Support": "🦼",
        "Public Health": "🏛",
        "Blood & Health Support": "💧",
        "Social Welfare": "👥"
    };

    // =====================================================
    // SECURITY HELPERS
    // =====================================================
    function escapeHtml(str) {
        if (str === null || str === undefined) return "";
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    function sanitizeUrl(url) {
        if (!url) return "";
        const trimmed = String(url).trim();
        if (/^https?:\/\//i.test(trimmed)) {
            return encodeURI(trimmed);
        }
        return "";
    }

    function getCategoryIcon(category) {
        return categoryIcons[category] || "🏥";
    }

    // =====================================================
    // DYNAMIC DROPDOWN POPULATION
    // =====================================================
    function populateDropdowns(schemes) {
        if (!schemes || !Array.isArray(schemes)) return;

        const categories = new Set();
        const schemeTypes = new Set();
        const states = new Set();

        schemes.forEach(item => {
            if (item.category && item.category.trim()) categories.add(item.category.trim());
            if (item.scheme_type && item.scheme_type.trim()) schemeTypes.add(item.scheme_type.trim());
            if (item.state && item.state.trim() && item.state.trim().toLowerCase() !== "all india") {
                states.add(item.state.trim());
            }
        });

        // Populate Category Dropdown
        if (categoryFilter) {
            const currentCat = categoryFilter.value || "All Categories";
            categoryFilter.innerHTML = '<option value="All Categories">All Categories</option>';
            Array.from(categories).sort().forEach(cat => {
                const opt = document.createElement("option");
                opt.value = cat;
                opt.textContent = cat;
                categoryFilter.appendChild(opt);
            });
            categoryFilter.value = currentCat;
        }

        // Populate Scheme Type Dropdown
        if (schemeTypeFilter) {
            const currentType = schemeTypeFilter.value || "All Schemes";
            schemeTypeFilter.innerHTML = '<option value="All Schemes">All Schemes</option>';
            Array.from(schemeTypes).sort().forEach(type => {
                const opt = document.createElement("option");
                opt.value = type;
                opt.textContent = type;
                schemeTypeFilter.appendChild(opt);
            });
            schemeTypeFilter.value = currentType;
        }

        // Populate State Dropdown
        if (stateFilter) {
            const currentState = stateFilter.value || "All States";
            stateFilter.innerHTML = `
                <option value="All States">All States</option>
                <option value="All India">All India</option>
            `;
            Array.from(states).sort().forEach(state => {
                const opt = document.createElement("option");
                opt.value = state;
                opt.textContent = state;
                stateFilter.appendChild(opt);
            });
            stateFilter.value = currentState;
        }
    }

    // =====================================================
    // LOAD SCHEMES (REST API)
    // =====================================================
    async function loadSchemes() {
        renderLoadingState();

        try {
            const response = await fetch("http://localhost:8080/api/government-schemes");

            if (!response.ok) {
                throw new Error(`Server returned HTTP ${response.status}`);
            }

            const data = await response.json();

            if (!Array.isArray(data)) {
                throw new Error("Invalid response format: expected an array of schemes");
            }

            allSchemes = data;
            populateDropdowns(allSchemes);
            filterSchemes();

        } catch (error) {
            console.error("Error loading government schemes:", error);
            renderErrorState("Unable to connect to the Government Schemes API.", () => loadSchemes());
        }
    }

    // =====================================================
    // RENDER HELPERS
    // =====================================================
    function renderLoadingState() {
        if (schemeGrid) {
            schemeGrid.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">⏳</div>
                    <h3>Loading Government Schemes...</h3>
                    <p>Fetching official health schemes catalogue.</p>
                </div>
            `;
        }
        if (loadMoreContainer) loadMoreContainer.style.display = "none";
        if (schemeCounter) schemeCounter.textContent = "";
    }

    function renderErrorState(message, retryFn) {
        if (!schemeGrid) return;

        schemeGrid.innerHTML = `
            <div class="error-state">
                <div class="empty-state-icon">⚠️</div>
                <h3>Unable to Load Schemes</h3>
                <p>${escapeHtml(message)}</p>
                <button class="retry-btn" id="retryLoadBtn">Retry Loading</button>
            </div>
        `;

        const retryBtn = document.getElementById("retryLoadBtn");
        if (retryBtn && retryFn) {
            retryBtn.addEventListener("click", retryFn);
        }

        if (loadMoreContainer) loadMoreContainer.style.display = "none";
        if (schemeCounter) schemeCounter.textContent = "";
    }

    function renderEmptyState() {
        if (!schemeGrid) return;

        schemeGrid.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">🔍</div>
                <h3>No Government Schemes Found</h3>
                <p>We couldn't find any schemes matching your search or filter criteria. Try adjusting your query or resetting filters.</p>
                <button class="reset-filters-btn" id="emptyResetBtn">Reset All Filters</button>
            </div>
        `;

        const emptyResetBtn = document.getElementById("emptyResetBtn");
        if (emptyResetBtn) {
            emptyResetBtn.addEventListener("click", resetAllFilters);
        }

        if (loadMoreContainer) loadMoreContainer.style.display = "none";
        if (schemeCounter) schemeCounter.textContent = "(0 schemes)";
    }

    // =====================================================
    // DISPLAY SCHEMES (WITH PAGINATION)
    // =====================================================
    function displaySchemes(schemes, append = false) {
        if (!schemeGrid) return;

        if (!schemes || schemes.length === 0) {
            renderEmptyState();
            return;
        }

        if (!append) {
            schemeGrid.innerHTML = "";
        }

        const startIndex = append ? schemeGrid.children.length : 0;
        const toRender = schemes.slice(startIndex, displayedCount);

        const fragment = document.createDocumentFragment();

        toRender.forEach(scheme => {
            const card = document.createElement("div");
            card.className = "scheme-card";

            const safeName = escapeHtml(scheme.scheme_name || "Government Health Scheme");
            const safeDesc = escapeHtml(scheme.short_description || "Information about this government scheme.");
            const safeCat = escapeHtml(scheme.category || "Health & Wellness");
            const safeType = escapeHtml(scheme.scheme_type || "Central Scheme");
            const safeState = escapeHtml(scheme.state || "All India");
            const safeEligibility = escapeHtml(
                scheme.eligibility ||
                "Please check the official government website for current eligibility criteria."
            );
            const safeUrl = sanitizeUrl(scheme.official_url);
            const icon = getCategoryIcon(scheme.category);

            const isStateScheme = (scheme.scheme_type || "").toLowerCase().includes("state");
            const typeBadgeClass = isStateScheme ? "scheme-type state" : "scheme-type";

            card.innerHTML = `
                <div class="scheme-card-header">
                    <div class="scheme-icon">
                        ${icon}
                    </div>
                    <span class="${typeBadgeClass}">
                        ${safeType}
                    </span>
                </div>

                <h3>${safeName}</h3>

                <p>${safeDesc}</p>

                <div class="scheme-info">
                    <div>
                        <small>Category</small>
                        <strong>${safeCat}</strong>
                    </div>
                    <div>
                        <small>State</small>
                        <strong>${safeState}</strong>
                    </div>
                </div>

                <div class="eligibility">
                    <h4>Eligibility</h4>
                    <p>${safeEligibility}</p>
                </div>

                <div class="scheme-actions">
                    <button class="view-details-btn" data-id="${escapeHtml(scheme.id)}">
                        View Details
                    </button>
                    ${
                        safeUrl
                            ? `<a href="${safeUrl}" target="_blank" rel="noopener noreferrer" class="official-btn">
                                 Get More Information ↗
                               </a>`
                            : `<span class="official-btn" style="opacity: 0.6; cursor: not-allowed;">Official Site N/A</span>`
                    }
                </div>
            `;

            fragment.appendChild(card);
        });

        schemeGrid.appendChild(fragment);

        // Update Counter
        const total = schemes.length;
        const currentShown = Math.min(displayedCount, total);
        if (schemeCounter) {
            schemeCounter.textContent = `(Showing ${currentShown} of ${total} schemes)`;
        }

        // Update Load More Button visibility
        if (loadMoreContainer) {
            if (displayedCount < total) {
                loadMoreContainer.style.display = "block";
            } else {
                loadMoreContainer.style.display = "none";
            }
        }
    }

    // =====================================================
    // FILTER LOGIC
    // =====================================================
    function filterSchemes(resetPagination = true) {
        if (resetPagination) {
            displayedCount = BATCH_SIZE;
        }

        const searchText = searchInput ? searchInput.value.trim().toLowerCase() : "";
        const selectedCategory = categoryFilter ? categoryFilter.value : "All Categories";
        const selectedType = schemeTypeFilter ? schemeTypeFilter.value : "All Schemes";
        const selectedState = stateFilter ? stateFilter.value : "All States";

        currentFilteredSchemes = allSchemes.filter(scheme => {
            // Search across name, description, category, scheme_type, state, and eligibility
            const searchableText = `
                ${scheme.scheme_name || ""}
                ${scheme.short_description || ""}
                ${scheme.category || ""}
                ${scheme.scheme_type || ""}
                ${scheme.state || ""}
                ${scheme.eligibility || ""}
            `.toLowerCase();

            const matchesSearch = searchText === "" || searchableText.includes(searchText);

            // Category Dropdown
            const matchesCategoryDropdown =
                selectedCategory === "All Categories" || scheme.category === selectedCategory;

            // Category Quick Buttons
            const matchesCategoryButton =
                activeCategory === "All Schemes" || scheme.category === activeCategory;

            // Scheme Type
            const matchesType =
                selectedType === "All Schemes" || scheme.scheme_type === selectedType;

            // State
            const matchesState =
                selectedState === "All States" ||
                scheme.state === selectedState ||
                (selectedState !== "All India" && scheme.state === "All India");

            return (
                matchesSearch &&
                matchesCategoryDropdown &&
                matchesCategoryButton &&
                matchesType &&
                matchesState
            );
        });

        displaySchemes(currentFilteredSchemes, false);
    }

    // =====================================================
    // RESET ALL FILTERS
    // =====================================================
    function resetAllFilters(event) {
        if (event && event.preventDefault) {
            event.preventDefault();
        }

        if (searchInput) searchInput.value = "";
        if (categoryFilter) categoryFilter.value = "All Categories";
        if (schemeTypeFilter) schemeTypeFilter.value = "All Schemes";
        if (stateFilter) stateFilter.value = "All States";

        activeCategory = "All Schemes";
        categoryButtons.forEach(btn => btn.classList.remove("active"));
        const allBtn = Array.from(categoryButtons).find(btn =>
            (btn.dataset.category || btn.querySelector("span")?.textContent.trim()) === "All Schemes"
        );
        if (allBtn) allBtn.classList.add("active");

        displayedCount = BATCH_SIZE;
        filterSchemes(true);

        if (schemeGrid) {
            schemeGrid.scrollIntoView({ behavior: "smooth", block: "start" });
        }
    }

    // =====================================================
    // EVENT LISTENERS: SEARCH & FILTERS
    // =====================================================
    if (searchButton) {
        searchButton.addEventListener("click", () => filterSchemes(true));
    }

    if (searchInput) {
        searchInput.addEventListener("keydown", event => {
            if (event.key === "Enter") {
                filterSchemes(true);
            }
        });
        searchInput.addEventListener("input", () => {
            if (searchInput.value.trim() === "") {
                filterSchemes(true);
            }
        });
    }

    if (categoryFilter) {
        categoryFilter.addEventListener("change", () => {
            // Synchronize active category buttons
            activeCategory = "All Schemes";
            categoryButtons.forEach(btn => btn.classList.remove("active"));
            const matchingBtn = Array.from(categoryButtons).find(btn =>
                (btn.dataset.category || btn.querySelector("span")?.textContent.trim()) === categoryFilter.value
            );
            if (matchingBtn) {
                matchingBtn.classList.add("active");
                activeCategory = categoryFilter.value;
            } else {
                const allBtn = Array.from(categoryButtons).find(btn =>
                    (btn.dataset.category || btn.querySelector("span")?.textContent.trim()) === "All Schemes"
                );
                if (allBtn) allBtn.classList.add("active");
            }
            filterSchemes(true);
        });
    }

    if (schemeTypeFilter) {
        schemeTypeFilter.addEventListener("change", () => filterSchemes(true));
    }

    if (stateFilter) {
        stateFilter.addEventListener("change", () => filterSchemes(true));
    }

    // Category Buttons click handler
    categoryButtons.forEach(button => {
        button.addEventListener("click", () => {
            categoryButtons.forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");

            activeCategory =
                button.dataset.category ||
                button.querySelector("span")?.textContent.trim() ||
                "All Schemes";

            if (categoryFilter) {
                categoryFilter.value =
                    activeCategory === "All Schemes" ? "All Categories" : activeCategory;
                // If the selected category is not in the dropdown options, fallback to All Categories
                if (activeCategory !== "All Schemes" && categoryFilter.value !== activeCategory) {
                    categoryFilter.value = "All Categories";
                }
            }

            filterSchemes(true);
        });
    });

    if (viewAllSchemes) {
        viewAllSchemes.addEventListener("click", resetAllFilters);
    }

    // Load More Button
    if (loadMoreBtn) {
        loadMoreBtn.addEventListener("click", () => {
            displayedCount += BATCH_SIZE;
            displaySchemes(currentFilteredSchemes, false);
        });
    }

    // =====================================================
    // VIEW DETAILS MODAL
    // =====================================================
    document.addEventListener("click", event => {
        const button = event.target.closest(".view-details-btn");
        if (!button) return;

        const schemeId = button.dataset.id;
        openSchemeDetails(schemeId);
    });

    function openSchemeDetails(schemeId) {
        const scheme = allSchemes.find(item => String(item.id) === String(schemeId));
        if (!scheme || !schemeModal || !schemeModalBody) return;

        const safeName = escapeHtml(scheme.scheme_name || "Government Health Scheme");
        const safeDesc = escapeHtml(scheme.short_description || "Information about this government scheme.");
        const safeCat = escapeHtml(scheme.category || "Health & Wellness");
        const safeType = escapeHtml(scheme.scheme_type || "Central Scheme");
        const safeState = escapeHtml(scheme.state || "All India");
        const safeEligibility = escapeHtml(
            scheme.eligibility ||
            "Please check the official government website for current eligibility criteria."
        );
        const safeUrl = sanitizeUrl(scheme.official_url);
        const icon = getCategoryIcon(scheme.category);

        const isStateScheme = (scheme.scheme_type || "").toLowerCase().includes("state");
        const typeBadgeClass = isStateScheme ? "scheme-type state" : "scheme-type";

        schemeModalBody.innerHTML = `
            <div class="modal-icon">
                ${icon}
            </div>

            <h2>${safeName}</h2>

            <span class="${typeBadgeClass}">
                ${safeType}
            </span>

            <p class="modal-description">
                ${safeDesc}
            </p>

            <div class="modal-info">
                <div>
                    <strong>Category</strong>
                    <span>${safeCat}</span>
                </div>
                <div>
                    <strong>Scheme Type</strong>
                    <span>${safeType}</span>
                </div>
                <div>
                    <strong>State</strong>
                    <span>${safeState}</span>
                </div>
            </div>

            <div class="modal-eligibility">
                <h3>Eligibility</h3>
                <p>${safeEligibility}</p>
            </div>

            ${
                safeUrl
                    ? `<a href="${safeUrl}" target="_blank" rel="noopener noreferrer" class="official-btn">
                         Get More Information ↗
                       </a>`
                    : `<span class="official-btn" style="opacity: 0.6; cursor: not-allowed;">Official Website Unavailable</span>`
            }

            <div class="modal-source">
                <span>🏛</span>
                <small>Source: Government of India / myScheme / National Health Portal</small>
            </div>
        `;

        schemeModal.classList.add("show");
    }

    // Modal Close Triggers
    if (closeSchemeModal) {
        closeSchemeModal.addEventListener("click", () => {
            schemeModal.classList.remove("show");
        });
    }

    if (schemeModal) {
        schemeModal.addEventListener("click", event => {
            if (event.target === schemeModal) {
                schemeModal.classList.remove("show");
            }
        });
    }

    document.addEventListener("keydown", event => {
        if (event.key === "Escape" && schemeModal && schemeModal.classList.contains("show")) {
            schemeModal.classList.remove("show");
        }
    });

    // =====================================================
    // INITIAL LOAD
    // =====================================================
    loadSchemes();
});