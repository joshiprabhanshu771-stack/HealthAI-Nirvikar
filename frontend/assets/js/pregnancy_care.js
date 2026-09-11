/**
 * HealthAI — Pregnancy Care & Maternal Wellness Module Engine
 * Handles Due Date Calculation, Trimester Navigation, Nutrition Filtering,
 * Maternal Red-Flags, and Interactive Hospital Bag Checklists.
 */

(function () {
  'use strict';

  const API_BASE = '/api/pregnancy-care';

  // Fallback Trimester Data
  const fallbackTrimesters = [
    {
      trimesterNumber: 1,
      trimesterName: 'First Trimester',
      weekRange: 'Weeks 1 – 12',
      summary: 'The critical foundational phase where all vital organs, neural tube, and early structures begin to form.',
      babyDevelopment: 'Heart begins beating around weeks 5–6. Neural tube closes to form brain and spine. Tiny buds develop into distinct fingers and toes.',
      motherChanges: 'hCG surge, nausea/morning sickness, heightened smell, breast tenderness, fatigue, and frequent urination.',
      keyNutritionTips: '400–600 mcg Folic Acid daily, Vitamin B6 for nausea, gentle hydration, small frequent nutrient-dense meals.',
      recommendedTests: 'Dating Ultrasound (Wk 6–8), Nuchal Translucency (NT) Scan & Combined Dual Marker Screen (Wk 11–13), Complete Blood Count & Thyroid.',
      fruitSizeComparison: 'Poppy Seed to Plum',
      icon: 'fa-seedling'
    },
    {
      trimesterNumber: 2,
      trimesterName: 'Second Trimester',
      weekRange: 'Weeks 13 – 26',
      summary: 'Known as the "Golden Trimester" as morning sickness subsides and maternal energy substantially improves.',
      babyDevelopment: 'Baby begins hearing external sounds, develops fingerprints, blinks, and starts practicing breathing motions. Fetal kicks ("quickening") start between weeks 18–22.',
      motherChanges: 'Baby bump becomes visible, skin changes (linea nigra, glow), mild round ligament stretching pains, increased appetite.',
      keyNutritionTips: '27 mg elemental iron daily for hemoglobin, 1000 mg calcium + 600 IU Vit D for skeletal density, Omega-3 (DHA) for brain growth.',
      recommendedTests: 'Comprehensive Level-II Target Anomaly Scan (Wk 18–20), Oral Glucose Tolerance Test (OGTT) for gestational diabetes (Wk 24–28).',
      fruitSizeComparison: 'Lemon to Papaya / Cauliflower',
      icon: 'fa-baby'
    },
    {
      trimesterNumber: 3,
      trimesterName: 'Third Trimester',
      weekRange: 'Weeks 27 – 40+',
      summary: 'The final preparation period where baby rapidly gains protective fat layers and organs mature for birth.',
      babyDevelopment: 'Lungs mature surfactant production, eyes open and close, bones harden, and baby assumes cephalic (head-down) position.',
      motherChanges: 'Shortness of breath, back strain, Braxton Hicks practice contractions, frequent urination, ankle swelling, nesting instinct.',
      keyNutritionTips: '75–100g daily high-quality protein, dietary fiber to support digestion, electrolyte hydration, magnesium for leg cramps.',
      recommendedTests: 'Fetal Growth Ultrasound & Doppler (Wk 32–36), Non-Stress Test (NST), Group B Strep (GBS) screening (Wk 36–37).',
      fruitSizeComparison: 'Eggplant to Watermelon / Pumpkin',
      icon: 'fa-heart-pulse'
    }
  ];

  // Fallback Nutrition Data
  const fallbackNutrition = [
    {
      id: 1,
      nutrientName: 'Folic Acid (Folate)',
      category: 'Essential',
      dailyTarget: '400 – 600 mcg',
      whyNeeded: 'Crucial for early neural tube development; prevents spina bifida and congenital brain defects.',
      richSources: 'Dark leafy greens (spinach, methi), fortified whole grains, lentils, chickpeas, oranges.',
      isSafe: true,
      cautionNotes: 'Begin before conception or immediately upon pregnancy confirmation.',
      icon: 'fa-leaf'
    },
    {
      id: 2,
      nutrientName: 'Elemental Iron',
      category: 'Essential',
      dailyTarget: '27 mg / day',
      whyNeeded: 'Doubles maternal blood volume and ensures oxygen delivery across the placenta.',
      richSources: 'Spinach, lentils, beans, fortified cereals, poultry, dates, pumpkin seeds.',
      isSafe: true,
      cautionNotes: 'Take with Vitamin C (citrus/amla). Do not take simultaneously with calcium or tea.',
      icon: 'fa-droplet'
    },
    {
      id: 3,
      nutrientName: 'Calcium & Vitamin D',
      category: 'Essential',
      dailyTarget: '1000 mg / 600 IU',
      whyNeeded: 'Builds fetal skeletal structure, heart rhythm, and tooth buds without leeching maternal bone density.',
      richSources: 'Pasteurized milk, curd/yogurt, paneer, fortified ragi, sesame seeds, sunlight.',
      isSafe: true,
      cautionNotes: 'Separate calcium intake from iron supplements by at least 2 hours.',
      icon: 'fa-bone'
    },
    {
      id: 4,
      nutrientName: 'DHA & Omega-3',
      category: 'Essential',
      dailyTarget: '200 – 300 mg DHA',
      whyNeeded: 'Powers rapid 3rd-trimester fetal brain tissue synthesis and retinal eye health.',
      richSources: 'Walnuts, chia seeds, flaxseeds, algae oil, cooked low-mercury fish (salmon).',
      isSafe: true,
      cautionNotes: 'Avoid high-mercury predatory fish (shark, swordfish, king mackerel).',
      icon: 'fa-brain'
    },
    {
      id: 5,
      nutrientName: 'Cooked Eggs & Lean Proteins',
      category: 'Safe Food',
      dailyTarget: '75 – 100 g protein',
      whyNeeded: 'Supplies building block amino acids and choline for brain development.',
      richSources: 'Well-cooked eggs, thoroughly boiled lentils/dals, paneer, roasted chana, chicken breast.',
      isSafe: true,
      cautionNotes: 'Always ensure eggs are hard-cooked with firm yolks and whites. Never eat raw batter.',
      icon: 'fa-egg'
    },
    {
      id: 6,
      nutrientName: 'Unpasteurized Raw Dairy & Soft Cheeses',
      category: 'Avoid Food',
      dailyTarget: 'Strict 0 mg',
      whyNeeded: 'High risk of Listeria monocytogenes which can cross the placenta and cause pregnancy loss.',
      richSources: 'Unpasteurized milk, unboiled farm milk, imported raw soft cheeses (brie, camembert).',
      isSafe: false,
      cautionNotes: 'Always verify dairy packaging says "Pasteurized". Boil fresh milk thoroughly.',
      icon: 'fa-ban'
    },
    {
      id: 7,
      nutrientName: 'Raw / Undercooked Seafood & Meats',
      category: 'Avoid Food',
      dailyTarget: 'Strict 0 mg',
      whyNeeded: 'Can harbor Salmonella, Toxoplasmosis parasites, and harmful bacteria.',
      richSources: 'Raw sushi, rare cooked steaks, unwashed deli cold cuts, raw shellfish.',
      isSafe: false,
      cautionNotes: 'Cook all meats and fish thoroughly until steaming hot throughout (>165°F / 74°C).',
      icon: 'fa-triangle-exclamation'
    },
    {
      id: 8,
      nutrientName: 'Excess Caffeine & Alcohol',
      category: 'Avoid Food',
      dailyTarget: 'Max <200mg Caffeine / Zero Alcohol',
      whyNeeded: 'Alcohol causes Fetal Alcohol Syndrome. High caffeine restricts fetal growth and blood flow.',
      richSources: 'Energy drinks, concentrated espressos, beer, wine, liquors.',
      isSafe: false,
      cautionNotes: 'Limit tea/coffee to 1 small cup/day. Completely avoid any alcohol throughout pregnancy.',
      icon: 'fa-mug-hot'
    }
  ];

  // Fallback Warning Signs
  const fallbackWarningSigns = [
    {
      symptomName: 'Vaginal Bleeding or Heavy Spotting',
      urgencyLevel: 'Immediate Emergency',
      description: 'Any bright red bleeding, clotting, or brownish discharge accompanied by sharp abdominal cramping.',
      actionRequired: 'Lie down immediately, do not use tampons, and proceed to the nearest emergency obstetric unit immediately.',
      icon: 'fa-droplet'
    },
    {
      symptomName: 'Severe Headache with Flashing Vision',
      urgencyLevel: 'Urgent Doctor Visit',
      description: 'Persistent throbbing headache unresponsive to rest, flashing light spots, or sudden facial/hand swelling.',
      actionRequired: 'Check blood pressure immediately to evaluate for Preeclampsia. Call your obstetrician promptly.',
      icon: 'fa-eye-slash'
    },
    {
      symptomName: 'Sudden Decrease in Fetal Movements',
      urgencyLevel: 'Urgent Doctor Visit',
      description: 'Baby kicks drop noticeably below 10 movements in 2 hours after week 28.',
      actionRequired: 'Drink a cold glass of sweet juice/water, lie on left side for 1 hour. If kicks remain low, visit hospital for NST scan.',
      icon: 'fa-baby'
    },
    {
      symptomName: 'Amniotic Fluid Leakage (Water Breaking)',
      urgencyLevel: 'Immediate Emergency',
      description: 'Continuous trickling or sudden gush of clear, yellowish, or greenish fluid before week 37.',
      actionRequired: 'Note the time and fluid color, put on a clean maternity pad, do not insert anything, and head to delivery ward.',
      icon: 'fa-water'
    },
    {
      symptomName: 'High Fever (> 100.4°F / 38°C) with Shivering',
      urgencyLevel: 'Urgent Doctor Visit',
      description: 'Elevated body temperature, chills, back pain, or burning sensation while urinating.',
      actionRequired: 'Consult healthcare provider for urine culture and safe antipyretic prescription to protect fetal environment.',
      icon: 'fa-temperature-arrow-up'
    }
  ];

  // Initialize Page
  document.addEventListener('DOMContentLoaded', () => {
    initCalculator();
    initTrimesterTabs();
    initNutritionFilter();
    initHospitalBagChecklist();
    loadWarningSigns();
  });

  /* ==========================================================================
     1. Gestational Age & Due Date Calculator
     ========================================================================== */
  function initCalculator() {
    const lmpInput = document.getElementById('lmpDateInput');
    const calcBtn = document.getElementById('calcDueDateBtn');
    const presetBtns = document.querySelectorAll('.preset-btn');

    if (!lmpInput || !calcBtn) return;

    // Set default date to 10 weeks ago
    const defaultDate = new Date();
    defaultDate.setDate(defaultDate.getDate() - 70);
    lmpInput.value = defaultDate.toISOString().split('T')[0];

    calcBtn.addEventListener('click', () => {
      calculateGestationalAge(lmpInput.value);
    });

    presetBtns.forEach(btn => {
      btn.addEventListener('click', (e) => {
        const weeksAgo = parseInt(e.currentTarget.dataset.weeks, 10);
        const d = new Date();
        d.setDate(d.getDate() - (weeksAgo * 7));
        lmpInput.value = d.toISOString().split('T')[0];
        calculateGestationalAge(lmpInput.value);
      });
    });

    // Auto-calculate on initial load
    calculateGestationalAge(lmpInput.value);
  }

  function calculateGestationalAge(lmpStr) {
    if (!lmpStr) return;

    const lmpDate = new Date(lmpStr);
    const today = new Date();

    if (lmpDate > today) {
      alert('Last menstrual period (LMP) date cannot be in the future.');
      return;
    }

    const diffTime = Math.abs(today - lmpDate);
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
    const weeks = Math.floor(diffDays / 7);
    const days = diffDays % 7;

    // Estimated Due Date: LMP + 280 days
    const eddDate = new Date(lmpDate);
    eddDate.setDate(eddDate.getDate() + 280);

    const remainingTime = eddDate - today;
    const remainingDays = Math.max(0, Math.floor(remainingTime / (1000 * 60 * 60 * 24)));
    const progress = Math.min(100, Math.max(0, Math.round((diffDays / 280) * 100)));

    let trimester = 1;
    let trimesterName = 'First Trimester';
    if (weeks >= 27) {
      trimester = 3;
      trimesterName = 'Third Trimester';
    } else if (weeks >= 13) {
      trimester = 2;
      trimesterName = 'Second Trimester';
    }

    const fruitInfo = getFruitComparison(weeks);

    // Update UI elements
    const eddDisplay = document.getElementById('eddDateDisplay');
    const weekDisplay = document.getElementById('currentWeekDisplay');
    const trimesterDisplay = document.getElementById('trimesterPillDisplay');
    const progressPercent = document.getElementById('progressPercentDisplay');
    const progressBar = document.getElementById('calcProgressBar');
    const daysLeftDisplay = document.getElementById('daysLeftDisplay');
    const fruitTitle = document.getElementById('fruitTitleDisplay');
    const fruitDesc = document.getElementById('fruitDescDisplay');

    if (eddDisplay) eddDisplay.textContent = eddDate.toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' });
    if (weekDisplay) weekDisplay.textContent = `Week ${weeks} + ${days} days`;
    if (trimesterDisplay) trimesterDisplay.innerHTML = `<i class="fa-solid fa-sparkles"></i> ${trimesterName}`;
    if (progressPercent) progressPercent.textContent = `${progress}% Complete`;
    if (progressBar) progressBar.style.width = `${progress}%`;
    if (daysLeftDisplay) daysLeftDisplay.textContent = `${remainingDays} Days`;
    if (fruitTitle) fruitTitle.textContent = `Baby Size: ${fruitInfo.title}`;
    if (fruitDesc) fruitDesc.textContent = fruitInfo.desc;

    // Auto-select corresponding trimester tab
    switchTrimesterTab(trimester);
  }

  function getFruitComparison(weeks) {
    if (weeks <= 4) return { title: 'Poppy Seed (< 1 mm)', desc: 'Blastocyst implanting into uterine lining; neural tube beginning.' };
    if (weeks <= 6) return { title: 'Sweet Pea (~5 mm)', desc: 'Heart begins rudimentary rhythmic beating; tiny limb buds.' };
    if (weeks <= 8) return { title: 'Raspberry (~1.6 cm)', desc: 'Eyelids, webbed fingers, and facial features forming.' };
    if (weeks <= 10) return { title: 'Strawberry (~3.1 cm)', desc: 'Vital organs functional; tail disappeared; bones hardening.' };
    if (weeks <= 12) return { title: 'Plum / Lime (~5.4 cm)', desc: 'Reflexes developing; kidneys producing urine; end of 1st trimester.' };
    if (weeks <= 16) return { title: 'Avocado (~11.6 cm)', desc: 'Baby can make facial expressions and eyes are sensitive to light.' };
    if (weeks <= 20) return { title: 'Banana (~25.6 cm)', desc: 'Midpoint of pregnancy! Baby can hear your voice and kick noticeably.' };
    if (weeks <= 24) return { title: 'Ear of Corn (~30 cm)', desc: 'Lungs practicing breathing movements; footprints and fingerprints formed.' };
    if (weeks <= 28) return { title: 'Eggplant (~37.6 cm)', desc: 'Eyes open and close; brain active with REM sleep patterns.' };
    if (weeks <= 32) return { title: 'Pineapple (~42.4 cm)', desc: 'Rapid weight gain; bones fully hardened except skull for delivery.' };
    if (weeks <= 36) return { title: 'Honeydew Melon (~47.4 cm)', desc: 'Head preparing into pelvic station (lightening); lungs mature.' };
    return { title: 'Watermelon / Pumpkin (~51 cm)', desc: 'Full term! Ready to meet the world any day now.' };
  }

  /* ==========================================================================
     2. Trimester Tabs & Milestone Journey
     ========================================================================== */
  function initTrimesterTabs() {
    const tabBtns = document.querySelectorAll('.tab-btn');
    tabBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        const tNum = parseInt(btn.dataset.trimester, 10);
        switchTrimesterTab(tNum);
      });
    });
  }

  function switchTrimesterTab(tNum) {
    const tabBtns = document.querySelectorAll('.tab-btn');
    const cards = document.querySelectorAll('.trimester-content-card');

    tabBtns.forEach(btn => {
      btn.classList.toggle('active', parseInt(btn.dataset.trimester, 10) === tNum);
    });

    cards.forEach(card => {
      card.classList.toggle('active', parseInt(card.dataset.trimester, 10) === tNum);
    });
  }

  /* ==========================================================================
     3. Prenatal Nutrition & Food Safety Filter
     ========================================================================== */
  function initNutritionFilter() {
    const filterBtns = document.querySelectorAll('.nutrition-filter-btn');
    renderNutritionCards(fallbackNutrition);

    filterBtns.forEach(btn => {
      btn.addEventListener('click', (e) => {
        filterBtns.forEach(b => b.classList.remove('active'));
        e.currentTarget.classList.add('active');
        const filter = e.currentTarget.dataset.filter;

        if (filter === 'All') {
          renderNutritionCards(fallbackNutrition);
        } else if (filter === 'Essential') {
          renderNutritionCards(fallbackNutrition.filter(item => item.category === 'Essential'));
        } else if (filter === 'Safe') {
          renderNutritionCards(fallbackNutrition.filter(item => item.isSafe && item.category !== 'Essential'));
        } else if (filter === 'Avoid') {
          renderNutritionCards(fallbackNutrition.filter(item => !item.isSafe));
        }
      });
    });
  }

  function renderNutritionCards(items) {
    const grid = document.getElementById('nutritionGrid');
    if (!grid) return;

    grid.innerHTML = items.map(item => {
      let cardClass = 'nutrition-card';
      let iconClass = 'nut-icon safe-icon';
      let badgeClass = 'nutrition-badge badge-safe';
      let badgeText = item.category;

      if (!item.isSafe) {
        cardClass += ' avoid-food';
        iconClass = 'nut-icon avoid-icon';
        badgeClass = 'nutrition-badge badge-avoid';
        badgeText = 'Avoid in Pregnancy';
      } else if (item.category === 'Essential') {
        iconClass = 'nut-icon essential-icon';
        badgeClass = 'nutrition-badge badge-essential';
        badgeText = 'Essential Nutrient';
      }

      return `
        <div class="${cardClass}">
          <div>
            <div class="nutrition-card-header">
              <div class="nutrition-title-grp">
                <div class="${iconClass}">
                  <i class="fa-solid ${item.icon}"></i>
                </div>
                <div>
                  <h4>${item.nutrientName}</h4>
                  <small style="color: var(--text-muted); font-weight: 500;">Target: ${item.dailyTarget}</small>
                </div>
              </div>
              <span class="${badgeClass}">${badgeText}</span>
            </div>
            <p>${item.whyNeeded}</p>
            <div class="nut-meta-item">
              <strong><i class="fa-solid fa-apple-whole text-teal"></i> Best Sources:</strong> ${item.richSources}
            </div>
          </div>
          <div class="nut-meta-item" style="margin-top: 0.75rem; background: ${item.isSafe ? '#f0fdf4' : '#fff1f2'}; border-left: 3px solid ${item.isSafe ? '#16a34a' : '#e11d48'};">
            <strong><i class="fa-solid fa-circle-info"></i> Note:</strong> ${item.cautionNotes}
          </div>
        </div>
      `;
    }).join('');
  }

  /* ==========================================================================
     4. Maternal Warning Signs
     ========================================================================== */
  function loadWarningSigns() {
    const grid = document.getElementById('warningSignsGrid');
    if (!grid) return;

    grid.innerHTML = fallbackWarningSigns.map(w => `
      <div class="warning-card">
        <span class="warning-urgency-tag">${w.urgencyLevel}</span>
        <h4><i class="fa-solid ${w.icon} text-pink"></i> ${w.symptomName}</h4>
        <p>${w.description}</p>
        <div class="action-highlight">
          <strong><i class="fa-solid fa-truck-medical"></i> Action:</strong> ${w.actionRequired}
        </div>
      </div>
    `).join('');
  }

  /* ==========================================================================
     5. Interactive Hospital Bag Checklist (Local Storage Persistence)
     ========================================================================== */
  const defaultChecklist = [
    { id: 'item-1', text: 'Hospital ID & Health Insurance Cards', checked: false, cat: 'Mom' },
    { id: 'item-2', text: 'Doctor Prescription Folder & Ultrasound Scans', checked: false, cat: 'Mom' },
    { id: 'item-3', text: 'Front-open Nursing Nightgowns & Robes (2-3 pairs)', checked: false, cat: 'Mom' },
    { id: 'item-4', text: 'Maternity Pads & High-waist Cotton Underwear', checked: false, cat: 'Mom' },
    { id: 'item-5', text: 'Newborn Onesies, Swaddles & Soft Baby Towels', checked: false, cat: 'Baby' },
    { id: 'item-6', text: 'Newborn Diapers & Fragrance-free Wipes', checked: false, cat: 'Baby' },
    { id: 'item-7', text: 'Baby Caps, Mittens & Booties', checked: false, cat: 'Baby' },
    { id: 'item-8', text: 'Long Phone Charger Cables & Power Bank', checked: false, cat: 'Partner' },
    { id: 'item-9', text: 'Healthy Energy Snacks & Reusable Water Bottle', checked: false, cat: 'Partner' },
    { id: 'item-10', text: 'Rear-facing Infant Car Seat (installed for departure)', checked: false, cat: 'Baby' }
  ];

  function initHospitalBagChecklist() {
    const container = document.getElementById('hospitalBagChecklist');
    if (!container) return;

    const saved = localStorage.getItem('healthai_hospital_bag');
    let items = saved ? JSON.parse(saved) : defaultChecklist;

    renderChecklist(container, items);
  }

  function renderChecklist(container, items) {
    container.innerHTML = items.map((item, idx) => `
      <label class="checklist-item ${item.checked ? 'checked' : ''}" data-index="${idx}">
        <input type="checkbox" ${item.checked ? 'checked' : ''}>
        <span>${item.text} <small style="color: var(--text-muted);">(${item.cat})</small></span>
      </label>
    `).join('');

    container.querySelectorAll('.checklist-item input[type="checkbox"]').forEach(chk => {
      chk.addEventListener('change', (e) => {
        const itemRow = e.target.closest('.checklist-item');
        const idx = parseInt(itemRow.dataset.index, 10);
        items[idx].checked = e.target.checked;
        itemRow.classList.toggle('checked', e.target.checked);
        localStorage.setItem('healthai_hospital_bag', JSON.stringify(items));
      });
    });
  }

})();
