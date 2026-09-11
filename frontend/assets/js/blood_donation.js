/**
 * HealthAI — Blood Donation & Emergency Donor Portal Engine
 * Handles Donor Registration, Emergency Request Posting,
 * Live Search/Filter, Compatibility Matrix, and Eligibility Quiz.
 */

(function () {
  'use strict';

  const API_BASE = '/api/blood-donation';

  // Fallback Datasets (In-memory & localStorage persistent)
  let donorsList = [
    { id: 1, fullName: 'Rohit Sharma', bloodGroup: 'O+', age: 29, gender: 'Male', city: 'Indore', phone: '+91 98260 11223', email: 'rohit.sharma@example.com', lastDonationDate: '2025-11-10', isAvailable: true, totalDonations: 4 },
    { id: 2, fullName: 'Ananya Verma', bloodGroup: 'A+', age: 24, gender: 'Female', city: 'Bhopal', phone: '+91 98930 44556', email: 'ananya.verma@example.com', lastDonationDate: '2025-12-05', isAvailable: true, totalDonations: 2 },
    { id: 3, fullName: 'Vikramaditya Joshi', bloodGroup: 'B+', age: 32, gender: 'Male', city: 'Dewas', phone: '+91 94250 77889', email: 'vikram.joshi@example.com', lastDonationDate: '2025-10-18', isAvailable: true, totalDonations: 6 },
    { id: 4, fullName: 'Pooja Nair', bloodGroup: 'O-', age: 27, gender: 'Female', city: 'Indore', phone: '+91 97550 22334', email: 'pooja.nair@example.com', lastDonationDate: '2025-08-20', isAvailable: true, totalDonations: 3 },
    { id: 5, fullName: 'Sameer Khan', bloodGroup: 'AB+', age: 35, gender: 'Male', city: 'Ujjain', phone: '+91 98270 99881', email: 'sameer.khan@example.com', lastDonationDate: '2025-12-28', isAvailable: true, totalDonations: 5 },
    { id: 6, fullName: 'Neha Kulkarni', bloodGroup: 'A-', age: 26, gender: 'Female', city: 'Indore', phone: '+91 98931 66778', email: 'neha.kulkarni@example.com', lastDonationDate: '2025-09-15', isAvailable: true, totalDonations: 1 },
    { id: 7, fullName: 'Aman Gupta', bloodGroup: 'B-', age: 30, gender: 'Male', city: 'Bhopal', phone: '+91 94066 33445', email: 'aman.gupta@example.com', lastDonationDate: '2025-11-01', isAvailable: true, totalDonations: 3 },
    { id: 8, fullName: 'Priya Patidar', bloodGroup: 'AB-', age: 28, gender: 'Female', city: 'Dewas', phone: '+91 97520 88990', email: 'priya.patidar@example.com', lastDonationDate: '2025-07-14', isAvailable: true, totalDonations: 2 }
  ];

  let requestsList = [
    { id: 1, patientName: 'Sunita Devi', bloodGroup: 'O-', unitsNeeded: 2, hospitalName: 'MY Hospital, Indore', city: 'Indore', contactPerson: 'Dr. Alok Mishra', contactPhone: '+91 98261 00112', urgencyLevel: 'Critical', status: 'Open', requirementReason: 'Emergency caesarean section with acute maternal postpartum hemorrhage.' },
    { id: 2, patientName: 'Deepak Malviya', bloodGroup: 'B+', unitsNeeded: 3, hospitalName: 'Bhopal Memorial Hospital', city: 'Bhopal', contactPerson: 'Rajesh Malviya (Brother)', contactPhone: '+91 98932 11223', urgencyLevel: 'Urgent', status: 'Open', requirementReason: 'Undergoing cardiac bypass graft surgery scheduled tomorrow.' },
    { id: 3, patientName: 'Kavita Rathore', bloodGroup: 'A+', unitsNeeded: 1, hospitalName: 'District Civil Hospital, Dewas', city: 'Dewas', contactPerson: 'Suresh Rathore (Husband)', contactPhone: '+91 94251 44556', urgencyLevel: 'Critical', status: 'Open', requirementReason: 'Emergency road traffic trauma and splenic laceration blood loss.' },
    { id: 4, patientName: 'Master Aarav', bloodGroup: 'AB+', unitsNeeded: 2, hospitalName: 'CHL Hospitals, Indore', city: 'Indore', contactPerson: 'Meena Jain (Mother)', contactPhone: '+91 97551 77889', urgencyLevel: 'Standard', status: 'In Progress', requirementReason: 'Scheduled elective pediatric orthopedic bone surgery.' }
  ];

  const bloodBanksList = [
    { id: 1, bankName: 'Indore Red Cross Regional Blood Centre', city: 'Indore', address: 'Near Collectorate Office, Moti Tabela, Indore, MP', phone: '+91 731 2544108', operatingHours: '24/7 Open', verified: true },
    { id: 2, bankName: 'MY Hospital Model Blood Bank', city: 'Indore', address: 'Agra Bombay Rd, Sanyogitaganj, Indore, MP', phone: '+91 731 2527200', operatingHours: '24/7 Open', verified: true },
    { id: 3, bankName: 'Bhopal Red Cross Society Blood Bank', city: 'Bhopal', address: 'Red Cross Bhawan, Shivaji Nagar, Bhopal, MP', phone: '+91 755 2551108', operatingHours: '24/7 Open', verified: true },
    { id: 4, bankName: 'Gandhi Medical College Blood Bank', city: 'Bhopal', address: 'Sultania Rd, Royal Market, Bhopal, MP', phone: '+91 755 2540590', operatingHours: '24/7 Open', verified: true },
    { id: 5, bankName: 'District Hospital Blood Centre Dewas', city: 'Dewas', address: 'Civil Hospital Campus, AB Road, Dewas, MP', phone: '+91 7272 252108', operatingHours: '24/7 Open', verified: true }
  ];

  // Compatibility Rulebook
  const compatibilityMap = {
    'O-': {
      title: 'Universal Red Blood Cell Donor',
      fact: 'O- red blood cells can be transfused to any patient in emergency trauma when blood group is unknown.',
      donateTo: ['O-', 'O+', 'A-', 'A+', 'B-', 'B+', 'AB-', 'AB+'],
      receiveFrom: ['O-']
    },
    'O+': {
      title: 'Most Needed Red Cell Donor',
      fact: 'O+ is the most frequently transfused blood type across emergency trauma and surgical units.',
      donateTo: ['O+', 'A+', 'B+', 'AB+'],
      receiveFrom: ['O+', 'O-']
    },
    'A-': {
      title: 'Rh-Negative A Donor',
      fact: 'Can give red blood cells to all A and AB individuals regardless of Rh factor.',
      donateTo: ['A-', 'A+', 'AB-', 'AB+'],
      receiveFrom: ['A-', 'O-']
    },
    'A+': {
      title: 'A-Positive Recipient & Donor',
      fact: 'One of the most widely requested blood types for planned hospital surgeries.',
      donateTo: ['A+', 'AB+'],
      receiveFrom: ['A+', 'A-', 'O+', 'O-']
    },
    'B-': {
      title: 'Rare Rh-Negative B Donor',
      fact: 'Can donate red blood cells to B and AB patients across emergency departments.',
      donateTo: ['B-', 'B+', 'AB-', 'AB+'],
      receiveFrom: ['B-', 'O-']
    },
    'B+': {
      title: 'B-Positive Recipient & Donor',
      fact: 'Vital demand in oncology, hematology, and thalassemia patient management.',
      donateTo: ['B+', 'AB+'],
      receiveFrom: ['B+', 'B-', 'O+', 'O-']
    },
    'AB-': {
      title: 'Universal Plasma Donor',
      fact: 'AB- plasma contains no antibodies and can be safely given to all blood types.',
      donateTo: ['AB-', 'AB+'],
      receiveFrom: ['AB-', 'A-', 'B-', 'O-']
    },
    'AB+': {
      title: 'Universal Red Blood Cell Recipient',
      fact: 'AB+ individuals can safely receive red blood cells from any blood group.',
      donateTo: ['AB+'],
      receiveFrom: ['AB+', 'AB-', 'A+', 'A-', 'B+', 'B-', 'O+', 'O-']
    }
  };

  // DOM Loaded
  document.addEventListener('DOMContentLoaded', () => {
    loadStoredData();
    initPortalTabs();
    initDonorSearch();
    initRequestSearch();
    initModals();
    initDonorRegistration();
    initRequestPosting();
    initCompatibilityMatrix();
    initEligibilityQuiz();
    renderDonors(donorsList);
    renderRequests(requestsList);
    renderBloodBanks(bloodBanksList);
    updateStatsBanner();
  });

  function loadStoredData() {
    try {
      const storedDonors = localStorage.getItem('healthai_blood_donors');
      if (storedDonors) donorsList = JSON.parse(storedDonors);

      const storedReqs = localStorage.getItem('healthai_blood_requests');
      if (storedReqs) requestsList = JSON.parse(storedReqs);
    } catch (e) {
      console.warn('Storage read warning:', e);
    }
  }

  function saveStoredData() {
    try {
      localStorage.setItem('healthai_blood_donors', JSON.stringify(donorsList));
      localStorage.setItem('healthai_blood_requests', JSON.stringify(requestsList));
    } catch (e) {
      console.warn('Storage write warning:', e);
    }
  }

  function updateStatsBanner() {
    const totalDonorsEl = document.getElementById('statTotalDonors');
    const activeReqsEl = document.getElementById('statActiveRequests');
    const banksEl = document.getElementById('statTotalBanks');

    if (totalDonorsEl) totalDonorsEl.textContent = donorsList.length;
    if (activeReqsEl) activeReqsEl.textContent = requestsList.filter(r => r.status === 'Open').length;
    if (banksEl) banksEl.textContent = bloodBanksList.length;
  }

  /* ==========================================================================
     1. Portal Tab Navigation
     ========================================================================== */
  function initPortalTabs() {
    const tabBtns = document.querySelectorAll('.portal-tab-btn');
    const tabPanes = document.querySelectorAll('.tab-content-pane');

    tabBtns.forEach(btn => {
      btn.addEventListener('click', (e) => {
        tabBtns.forEach(b => b.classList.remove('active'));
        tabPanes.forEach(p => p.classList.remove('active'));

        e.currentTarget.classList.add('active');
        const targetId = e.currentTarget.dataset.tab;
        const targetPane = document.getElementById(targetId);
        if (targetPane) targetPane.classList.add('active');
      });
    });
  }

  /* ==========================================================================
     2. Donors Rendering & Searching
     ========================================================================== */
  function initDonorSearch() {
    const groupSelect = document.getElementById('donorFilterGroup');
    const cityInput = document.getElementById('donorFilterCity');
    const availCheckbox = document.getElementById('donorFilterAvail');

    const filterHandler = () => {
      const group = groupSelect ? groupSelect.value : 'All';
      const city = cityInput ? cityInput.value.trim().toLowerCase() : '';
      const onlyAvail = availCheckbox ? availCheckbox.checked : false;

      const filtered = donorsList.filter(d => {
        const matchGroup = group === 'All' || d.bloodGroup === group;
        const matchCity = !city || (d.city && d.city.toLowerCase().includes(city));
        const matchAvail = !onlyAvail || d.isAvailable;
        return matchGroup && matchCity && matchAvail;
      });

      renderDonors(filtered);
    };

    if (groupSelect) groupSelect.addEventListener('change', filterHandler);
    if (cityInput) cityInput.addEventListener('input', filterHandler);
    if (availCheckbox) availCheckbox.addEventListener('change', filterHandler);
  }

  function renderDonors(items) {
    const grid = document.getElementById('donorsGrid');
    if (!grid) return;

    if (items.length === 0) {
      grid.innerHTML = `
        <div style="grid-column: 1 / -1; text-align: center; padding: 3rem; background: var(--bg-light); border-radius: var(--radius-md);">
          <i class="fa-solid fa-user-slash" style="font-size: 2.5rem; color: var(--text-muted); margin-bottom: 0.75rem;"></i>
          <h4 style="color: var(--text-primary);">No Donors Found</h4>
          <p style="color: var(--text-secondary); font-size: 0.9rem;">Try adjusting your blood group or city filter.</p>
        </div>
      `;
      return;
    }

    grid.innerHTML = items.map(d => `
      <div class="donor-card">
        <div>
          <div class="donor-card-top">
            <div class="donor-avatar-group">
              <div class="blood-badge">${d.bloodGroup}</div>
              <div class="donor-name-title">
                <h4>${d.fullName}</h4>
                <span><i class="fa-solid fa-location-dot text-red"></i> ${d.city} &bull; Age ${d.age}</span>
              </div>
            </div>
            <span class="avail-pill ${d.isAvailable ? 'avail-yes' : 'avail-no'}">
              <i class="fa-solid ${d.isAvailable ? 'fa-circle-check' : 'fa-clock'}"></i> ${d.isAvailable ? 'Available' : 'Resting'}
            </span>
          </div>

          <div class="donor-details-list">
            <p><i class="fa-solid fa-venus-mars text-muted"></i> <strong>Gender:</strong> ${d.gender}</p>
            <p><i class="fa-solid fa-heart-circle-check text-red"></i> <strong>Total Donations:</strong> ${d.totalDonations} times</p>
            <p><i class="fa-solid fa-calendar-check text-teal"></i> <strong>Last Donated:</strong> ${d.lastDonationDate || 'First-time Donor'}</p>
          </div>
        </div>

        <div>
          <a href="tel:${d.phone}" class="btn-contact-donor">
            <i class="fa-solid fa-phone"></i> Call Donor (${d.phone})
          </a>
        </div>
      </div>
    `).join('');
  }

  /* ==========================================================================
     3. Emergency Requests Rendering & Searching
     ========================================================================== */
  function initRequestSearch() {
    const groupSelect = document.getElementById('reqFilterGroup');
    const urgencySelect = document.getElementById('reqFilterUrgency');

    const filterHandler = () => {
      const group = groupSelect ? groupSelect.value : 'All';
      const urgency = urgencySelect ? urgencySelect.value : 'All';

      const filtered = requestsList.filter(r => {
        const matchGroup = group === 'All' || r.bloodGroup === group;
        const matchUrgency = urgency === 'All' || r.urgencyLevel === urgency;
        return matchGroup && matchUrgency;
      });

      renderRequests(filtered);
    };

    if (groupSelect) groupSelect.addEventListener('change', filterHandler);
    if (urgencySelect) urgencySelect.addEventListener('change', filterHandler);
  }

  function renderRequests(items) {
    const grid = document.getElementById('requestsGrid');
    if (!grid) return;

    if (items.length === 0) {
      grid.innerHTML = `
        <div style="grid-column: 1 / -1; text-align: center; padding: 3rem; background: var(--bg-light); border-radius: var(--radius-md);">
          <i class="fa-solid fa-check-double" style="font-size: 2.5rem; color: #10b981; margin-bottom: 0.75rem;"></i>
          <h4 style="color: var(--text-primary);">No Pending Requests</h4>
          <p style="color: var(--text-secondary); font-size: 0.9rem;">All emergency requests in this criteria are currently fulfilled.</p>
        </div>
      `;
      return;
    }

    grid.innerHTML = items.map(r => {
      const isCritical = r.urgencyLevel === 'Critical';
      const isUrgent = r.urgencyLevel === 'Urgent';
      const cardClass = isCritical ? 'critical' : (isUrgent ? 'urgent' : 'standard');
      const pillClass = isCritical ? 'urgency-critical' : (isUrgent ? 'urgency-urgent' : 'urgency-standard');

      return `
        <div class="request-card ${cardClass}">
          <div>
            <div class="request-card-head">
              <div class="req-patient-title">
                <h4>${r.patientName}</h4>
                <span><i class="fa-solid fa-droplet text-red"></i> Needs ${r.unitsNeeded} Unit(s) of <strong>${r.bloodGroup}</strong></span>
              </div>
              <span class="urgency-pill ${pillClass}">${r.urgencyLevel}</span>
            </div>

            <div class="req-details-box">
              <p><strong><i class="fa-solid fa-hospital text-red"></i> Hospital:</strong> ${r.hospitalName} (${r.city})</p>
              <p><strong><i class="fa-solid fa-user-tag text-teal"></i> Contact:</strong> ${r.contactPerson} (<a href="tel:${r.contactPhone}" style="color: var(--blood-red); font-weight: 700;">${r.contactPhone}</a>)</p>
              <p style="margin-top: 0.35rem;"><strong><i class="fa-solid fa-circle-info text-amber"></i> Reason:</strong> ${r.requirementReason}</p>
            </div>
          </div>

          <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--border-color); padding-top: 0.75rem; margin-top: 0.5rem;">
            <span style="font-size: 0.8rem; font-weight: 700; color: ${r.status === 'Fulfilled' ? '#15803d' : '#b91c1c'};">
              <i class="fa-solid ${r.status === 'Fulfilled' ? 'fa-circle-check' : 'fa-circle-dot'}"></i> Status: ${r.status}
            </span>
            ${r.status !== 'Fulfilled' ? `
              <button class="btn-fulfill" data-id="${r.id}">
                <i class="fa-solid fa-hand-holding-heart"></i> Mark Fulfilled
              </button>
            ` : ''}
          </div>
        </div>
      `;
    }).join('');

    // Attach Fulfill Handlers
    document.querySelectorAll('.btn-fulfill').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const id = parseInt(e.currentTarget.dataset.id, 10);
        fulfillRequest(id);
      });
    });
  }

  function fulfillRequest(id) {
    const target = requestsList.find(r => r.id === id);
    if (target) {
      target.status = 'Fulfilled';
      saveStoredData();
      renderRequests(requestsList);
      updateStatsBanner();

      // Attempt async API update
      fetch(`${API_BASE}/requests/${id}/fulfill`, { method: 'PUT' }).catch(() => {});
    }
  }

  /* ==========================================================================
     4. Blood Banks Directory Rendering
     ========================================================================== */
  function renderBloodBanks(items) {
    const grid = document.getElementById('bloodBanksGrid');
    if (!grid) return;

    grid.innerHTML = items.map(b => `
      <div class="donor-card" style="border-top: 4px solid var(--blood-red);">
        <div>
          <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 0.5rem;">
            <h4 style="font-size: 1.1rem; font-weight: 800; color: var(--text-primary); margin: 0;">${b.bankName}</h4>
            <span class="avail-pill avail-yes"><i class="fa-solid fa-shield-halved"></i> Verified</span>
          </div>
          <p style="font-size: 0.86rem; color: var(--text-secondary); margin: 0.4rem 0;">
            <i class="fa-solid fa-location-dot text-red"></i> ${b.address}
          </p>
          <p style="font-size: 0.82rem; color: var(--text-muted); margin: 0.2rem 0;">
            <i class="fa-solid fa-clock text-teal"></i> Operating Hours: <strong>${b.operatingHours}</strong>
          </p>
        </div>
        <div style="margin-top: 1rem;">
          <a href="tel:${b.phone}" class="btn-contact-donor">
            <i class="fa-solid fa-phone-volume"></i> Call Blood Centre (${b.phone})
          </a>
        </div>
      </div>
    `).join('');
  }

  /* ==========================================================================
     5. Modals Management (Open / Close)
     ========================================================================== */
  function initModals() {
    const donorModal = document.getElementById('donorRegistrationModal');
    const reqModal = document.getElementById('bloodRequestModal');

    const openDonorBtns = document.querySelectorAll('.open-donor-modal-btn');
    const openReqBtns = document.querySelectorAll('.open-request-modal-btn');
    const closeBtns = document.querySelectorAll('.modal-close-btn');

    openDonorBtns.forEach(b => b.addEventListener('click', () => {
      if (donorModal) donorModal.classList.add('open');
    }));

    openReqBtns.forEach(b => b.addEventListener('click', () => {
      if (reqModal) reqModal.classList.add('open');
    }));

    closeBtns.forEach(b => b.addEventListener('click', () => {
      if (donorModal) donorModal.classList.remove('open');
      if (reqModal) reqModal.classList.remove('open');
    }));

    window.addEventListener('click', (e) => {
      if (e.target === donorModal) donorModal.classList.remove('open');
      if (e.target === reqModal) reqModal.classList.remove('open');
    });
  }

  /* ==========================================================================
     6. Donor Registration Submission
     ========================================================================== */
  function initDonorRegistration() {
    const form = document.getElementById('donorRegistrationForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const newDonor = {
        id: Date.now(),
        fullName: document.getElementById('donorName').value.trim(),
        bloodGroup: document.getElementById('donorBloodGroup').value,
        age: parseInt(document.getElementById('donorAge').value, 10),
        gender: document.getElementById('donorGender').value,
        city: document.getElementById('donorCity').value.trim(),
        phone: document.getElementById('donorPhone').value.trim(),
        email: document.getElementById('donorEmail').value.trim(),
        lastDonationDate: document.getElementById('donorLastDate').value || null,
        isAvailable: true,
        totalDonations: 1
      };

      donorsList.unshift(newDonor);
      saveStoredData();
      renderDonors(donorsList);
      updateStatsBanner();

      // Async backend call
      try {
        await fetch(`${API_BASE}/donors`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(newDonor)
        });
      } catch (err) {
        console.warn('Offline mode saving donor locally.');
      }

      form.reset();
      const modal = document.getElementById('donorRegistrationModal');
      if (modal) modal.classList.remove('open');

      alert('🎉 Thank you! You have been successfully registered as a voluntary blood donor.');
    });
  }

  /* ==========================================================================
     7. Emergency Blood Request Submission
     ========================================================================== */
  function initRequestPosting() {
    const form = document.getElementById('bloodRequestForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const newRequest = {
        id: Date.now(),
        patientName: document.getElementById('reqPatientName').value.trim(),
        bloodGroup: document.getElementById('reqBloodGroup').value,
        unitsNeeded: parseInt(document.getElementById('reqUnits').value, 10) || 1,
        hospitalName: document.getElementById('reqHospital').value.trim(),
        city: document.getElementById('reqCity').value.trim(),
        contactPerson: document.getElementById('reqContactName').value.trim(),
        contactPhone: document.getElementById('reqContactPhone').value.trim(),
        urgencyLevel: document.getElementById('reqUrgency').value,
        status: 'Open',
        requirementReason: document.getElementById('reqReason').value.trim()
      };

      requestsList.unshift(newRequest);
      saveStoredData();
      renderRequests(requestsList);
      updateStatsBanner();

      // Async backend call
      try {
        await fetch(`${API_BASE}/requests`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(newRequest)
        });
      } catch (err) {
        console.warn('Offline mode saving request locally.');
      }

      form.reset();
      const modal = document.getElementById('bloodRequestModal');
      if (modal) modal.classList.remove('open');

      // Switch to requests tab to see it live
      const reqTabBtn = document.querySelector('[data-tab="requestsPane"]');
      if (reqTabBtn) reqTabBtn.click();

      alert('🚨 Emergency blood requirement posted to the live network.');
    });
  }

  /* ==========================================================================
     8. Blood Compatibility Matrix Engine
     ========================================================================== */
  function initCompatibilityMatrix() {
    const bloodBtns = document.querySelectorAll('.blood-btn');
    renderCompatibility('O+');

    bloodBtns.forEach(btn => {
      btn.addEventListener('click', (e) => {
        bloodBtns.forEach(b => b.classList.remove('active'));
        e.currentTarget.classList.add('active');
        const group = e.currentTarget.dataset.group;
        renderCompatibility(group);
      });
    });
  }

  function renderCompatibility(group) {
    const data = compatibilityMap[group] || compatibilityMap['O+'];

    const titleEl = document.getElementById('compatTitle');
    const factEl = document.getElementById('compatFact');
    const donateGrid = document.getElementById('compatDonateGrid');
    const receiveGrid = document.getElementById('compatReceiveGrid');

    if (titleEl) titleEl.textContent = `${group} — ${data.title}`;
    if (factEl) factEl.textContent = data.fact;

    if (donateGrid) {
      donateGrid.innerHTML = data.donateTo.map(g => `<span class="compat-pill"><i class="fa-solid fa-droplet"></i> ${g}</span>`).join('');
    }

    if (receiveGrid) {
      receiveGrid.innerHTML = data.receiveFrom.map(g => `<span class="compat-pill"><i class="fa-solid fa-heart"></i> ${g}</span>`).join('');
    }
  }

  /* ==========================================================================
     9. Interactive Donor Eligibility Quiz
     ========================================================================== */
  function initEligibilityQuiz() {
    const quizCard = document.getElementById('eligibilityQuizBox');
    if (!quizCard) return;

    const answers = {};
    const quizBtns = quizCard.querySelectorAll('.quiz-btn');

    quizBtns.forEach(btn => {
      btn.addEventListener('click', (e) => {
        const qKey = e.currentTarget.dataset.q;
        const val = e.currentTarget.dataset.val === 'yes';

        // Deselect sibling
        const parent = e.currentTarget.parentElement;
        parent.querySelectorAll('.quiz-btn').forEach(b => b.classList.remove('selected-yes', 'selected-no'));

        if (val) {
          e.currentTarget.classList.add('selected-yes');
        } else {
          e.currentTarget.classList.add('selected-no');
        }

        answers[qKey] = val;
        evaluateQuiz(answers);
      });
    });
  }

  function evaluateQuiz(answers) {
    const resultBox = document.getElementById('quizResultBanner');
    if (!resultBox) return;

    if (Object.keys(answers).length < 4) return;

    // Rules:
    // q1 (Age 18-65): must be YES
    // q2 (Weight >= 45-50kg): must be YES
    // q3 (Donated in last 90 days): must be NO
    // q4 (Major surgery/infection/tattoo in 6m): must be NO

    const eligible = answers.age && answers.weight && !answers.recentDonation && !answers.infection;

    resultBox.style.display = 'block';
    if (eligible) {
      resultBox.style.background = '#dcfce7';
      resultBox.style.borderColor = '#86efac';
      resultBox.style.color = '#15803d';
      resultBox.innerHTML = `
        <h4 style="margin: 0 0 0.35rem 0; font-weight: 800;"><i class="fa-solid fa-circle-check"></i> Great News! You are Eligible to Donate Blood</h4>
        <p style="margin: 0; font-size: 0.88rem;">Your responses meet all national clinical safety guidelines. Click "Register as Donor" to save lives today!</p>
      `;
    } else {
      resultBox.style.background = '#fee2e2';
      resultBox.style.borderColor = '#fca5a5';
      resultBox.style.color = '#991b1b';
      resultBox.innerHTML = `
        <h4 style="margin: 0 0 0.35rem 0; font-weight: 800;"><i class="fa-solid fa-triangle-exclamation"></i> Temporarily Ineligible to Donate</h4>
        <p style="margin: 0; font-size: 0.88rem;">To safeguard donor and recipient health, you must be 18–65 yrs, &ge; 45kg, wait 90 days between donations, and be free of recent infections.</p>
      `;
    }
  }

})();
