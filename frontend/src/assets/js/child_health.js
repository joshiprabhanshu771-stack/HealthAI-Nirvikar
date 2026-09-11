/**
 * HealthAI — Child Health & Pediatric Care Module Engine
 * Handles Milestone Navigation, Immunization Tracking,
 * Pediatric Fever & Dehydration Triage, and Home Care Protocols.
 */

(function () {
  'use strict';

  // Fallback Milestones Data
  const fallbackMilestones = [
    {
      ageGroup: '0-3 Months',
      stageTitle: 'Newborn & Early Infant Reflexes',
      motorSkills: 'Lifts head briefly while on tummy; moves arms and legs equally; opens and shuts hands; strong rooting and grasp reflexes.',
      cognitiveSpeech: 'Startles at loud sounds; turns head toward maternal voice; coos and makes pleasant gurgling sounds; follows moving faces with eyes.',
      socialEmotional: 'Develops first spontaneous and responsive social smiles (by 6-8 weeks); calms to gentle touch or rocking; holds eye contact.',
      redFlagSigns: 'Does not react to loud noises; does not follow moving objects with eyes; does not smile by 8 weeks; floppy or excessively stiff muscle tone.',
      parentingTips: 'Encourage 3–5 minutes of supervised tummy time daily when awake; sing and talk gently during diaper changes.',
      icon: 'fa-baby'
    },
    {
      ageGroup: '4-6 Months',
      stageTitle: 'Rolling, Reaching & Babbling',
      motorSkills: 'Rolls from tummy to back; supports weight on legs when held upright; pushes up on elbows during tummy time; reaches and grasps toys with both hands.',
      cognitiveSpeech: 'Babbles with consonants (da-da, ba-ba); laughs out loud; responds to sounds by making sounds; brings objects to mouth for sensory exploration.',
      socialEmotional: 'Enjoys playful interaction (peek-a-boo); recognizes familiar family faces; shows excitement by kicking legs and waving arms.',
      redFlagSigns: 'Cannot hold head steady by 4 months; does not reach for objects; does not roll in either direction; shows no affection to primary caregivers.',
      parentingTips: 'Introduce colorful safe rattles; continue exclusive breastfeeding/formula until 6 completed months; talk back when baby babbles.',
      icon: 'fa-baby-carriage'
    },
    {
      ageGroup: '7-12 Months',
      stageTitle: 'Sitting, Crawling & First Words',
      motorSkills: 'Sits steadily without support; crawls on hands and knees; pulls up to stand while holding furniture; develops pincer grasp (thumb and forefinger).',
      cognitiveSpeech: 'Responds to own name; understands simple words like "No"; says first meaningful word ("Mama", "Dada", "Ball"); looks at picture books.',
      socialEmotional: 'Shows separation anxiety with unfamiliar strangers; waves "bye-bye" and claps hands; plays interactive games; points to objects.',
      redFlagSigns: 'Does not sit unsupported by 9 months; does not bear weight on legs; does not babble or respond to name; does not point to objects by 12 months.',
      parentingTips: 'Childproof the home thoroughly (plug electrical outlets, cushion sharp corners); introduce single-ingredient soft mashed foods after 6 months.',
      icon: 'fa-child'
    },
    {
      ageGroup: '1-3 Years',
      stageTitle: 'Toddler Independence & Language Leap',
      motorSkills: 'Walks steadily alone; runs, climbs stairs with support, kicks a ball, stacks 4-6 blocks, scribbles with crayons, drinks from an open cup.',
      cognitiveSpeech: 'Vocabulary grows from 10-20 words at 18m to 200+ words and 2-3 word sentences ("want milk", "big car") by age 2-3; follows 2-step instructions.',
      socialEmotional: 'Shows empathy to crying peers; engages in parallel and simple pretend play; begins toilet training readiness; displays growing independence.',
      redFlagSigns: 'Cannot walk by 18 months; speaks fewer than 6 words by 18m or no 2-word phrases by 2 years; loses previously acquired skills; lacks eye contact.',
      parentingTips: 'Read picture books daily; limit screen time to zero (under 2) or under 1 hr/day; offer healthy finger foods and establish bedtime routines.',
      icon: 'fa-child-reaching'
    },
    {
      ageGroup: '4-6 Years',
      stageTitle: 'Preschooler Socialization & Coordination',
      motorSkills: 'Hops on one foot; catches a bounced ball; cuts with safety scissors; dresses self independently; draws a person with 4+ body parts.',
      cognitiveSpeech: 'Speaks clearly in complete sentences; tells imaginative stories; understands counting, basic colors, and time concepts (yesterday, today, tomorrow).',
      socialEmotional: 'Plays cooperatively in groups; shares toys with peers; understands rules in games; displays a wide range of emotions and imagination.',
      redFlagSigns: 'Extremely aggressive or fearful behavior; cannot jump or balance on one foot; unintelligible speech to strangers; cannot copy a circle or cross.',
      parentingTips: 'Encourage outdoor active play (60 mins daily); foster social playdates; reinforce handwashing and oral hygiene habits twice daily.',
      icon: 'fa-people-group'
    },
    {
      ageGroup: '7-12 Years',
      stageTitle: 'School-Age Mastery & Reasoning',
      motorSkills: 'Refined fine motor skills (fluent handwriting, musical instruments, crafts); advanced athletic coordination and team sports proficiency.',
      cognitiveSpeech: 'Logical reasoning, structured problem-solving, reading fluency, understanding abstract concepts and moral fairness.',
      socialEmotional: 'Forms close peer friendships; understands social norms; develops personal hobbies, self-esteem, and independent decision-making.',
      redFlagSigns: 'Severe academic struggle unresponsive to support; extreme social withdrawal or bullying behavior; persistent sleep disturbances or depression.',
      parentingTips: 'Provide balanced nutritious meals with brain-boosting omega-3 & iron; ensure 9-11 hours of sleep; promote open communication about emotions.',
      icon: 'fa-graduation-cap'
    }
  ];

  // Fallback Vaccine Schedule
  const fallbackVaccines = [
    {
      id: 1,
      vaccineName: 'BCG (Bacille Calmette-Guérin)',
      targetAge: 'Birth',
      protectsAgainst: 'Tuberculosis (TB) Meningitis & Disseminated TB',
      doseNumber: 'Single Dose (0.1 ml)',
      routeOfAdmin: 'Intradermal (Left Upper Arm)',
      importanceNotes: 'Administered immediately after birth before hospital discharge. Causes a small harmless scar.',
      mandatoryStatus: 'Universal Essential'
    },
    {
      id: 2,
      vaccineName: 'OPV-0 & Hepatitis B-1',
      targetAge: 'Birth',
      protectsAgainst: 'Poliomyelitis & Perinatal Hepatitis B Infection',
      doseNumber: 'Birth Dose',
      routeOfAdmin: 'Oral Drops (OPV) & Intramuscular (Hep B)',
      importanceNotes: 'Hep B birth dose must be given within 24 hours of birth to prevent mother-to-child transmission.',
      mandatoryStatus: 'Universal Essential'
    },
    {
      id: 3,
      vaccineName: 'Pentavalent-1 + IPV-1 + Rota-1 + PCV-1',
      targetAge: '6 Weeks',
      protectsAgainst: 'Diphtheria, Pertussis, Tetanus, Hep B, Hib, Polio, Rotavirus & Pneumococcal',
      doseNumber: 'Primary Dose 1',
      routeOfAdmin: 'Intramuscular (Thigh) & Oral Drops',
      importanceNotes: 'Protects against 8 major fatal childhood bacterial and viral infections.',
      mandatoryStatus: 'Universal Essential'
    },
    {
      id: 4,
      vaccineName: 'Pentavalent-2 + Rota-2',
      targetAge: '10 Weeks',
      protectsAgainst: 'Diphtheria, Pertussis, Tetanus, Hep B, Hib & Rotavirus Diarrhea',
      doseNumber: 'Primary Dose 2',
      routeOfAdmin: 'Intramuscular & Oral Drops',
      importanceNotes: 'Reinforces immune antibodies established during the 6-week immunization.',
      mandatoryStatus: 'Universal Essential'
    },
    {
      id: 5,
      vaccineName: 'Pentavalent-3 + IPV-2 + Rota-3 + PCV-2',
      targetAge: '14 Weeks',
      protectsAgainst: 'Diphtheria, Pertussis, Tetanus, Hep B, Hib, Polio, Rotavirus & Pneumococcal',
      doseNumber: 'Primary Dose 3',
      routeOfAdmin: 'Intramuscular & Oral Drops',
      importanceNotes: 'Completes the primary infant immunization series for basic pathogens.',
      mandatoryStatus: 'Universal Essential'
    },
    {
      id: 6,
      vaccineName: 'MR-1 (Measles-Rubella) + PCV Booster + Vit A',
      targetAge: '9-12 Months',
      protectsAgainst: 'Measles, Rubella, Pneumococcal Pneumonia & Vitamin A Deficiency',
      doseNumber: '1st Dose MR + Booster PCV',
      routeOfAdmin: 'Subcutaneous & Oral Drops',
      importanceNotes: 'Crucial for preventing life-threatening measles complications, pneumonia, and blindness.',
      mandatoryStatus: 'Universal Essential'
    },
    {
      id: 7,
      vaccineName: 'MR-2 + DTP Booster-1 + OPV Booster',
      targetAge: '16-24 Months',
      protectsAgainst: 'Measles, Rubella, Diphtheria, Pertussis, Tetanus & Polio',
      doseNumber: '1st Booster Dose',
      routeOfAdmin: 'Intramuscular & Oral Drops',
      importanceNotes: 'Maintains protective immunity throughout active toddlerhood exploration.',
      mandatoryStatus: 'Universal Essential'
    },
    {
      id: 8,
      vaccineName: 'DTP Booster-2',
      targetAge: '5-6 Years',
      protectsAgainst: 'Diphtheria, Whooping Cough (Pertussis) & Tetanus',
      doseNumber: '2nd Booster Dose',
      routeOfAdmin: 'Intramuscular (Deltoid)',
      importanceNotes: 'Ensures high-level school-entry immunity against respiratory bacterial toxins.',
      mandatoryStatus: 'Universal Essential'
    },
    {
      id: 9,
      vaccineName: 'Td (Tetanus & adult Diphtheria) / HPV',
      targetAge: '10-16 Years',
      protectsAgainst: 'Tetanus, Diphtheria & Human Papillomavirus (Cervical Cancer prevention)',
      doseNumber: 'Adolescent Dose',
      routeOfAdmin: 'Intramuscular (Deltoid)',
      importanceNotes: 'Administered at age 10 and 16 for long-term adulthood tetanus protection and HPV defense.',
      mandatoryStatus: 'Universal Essential'
    }
  ];

  // Initialize Page
  document.addEventListener('DOMContentLoaded', () => {
    initMilestones();
    initVaccineFilter();
    initFeverTriage();
  });

  /* ==========================================================================
     1. Developmental Milestones Explorer
     ========================================================================== */
  function initMilestones() {
    const tabBtns = document.querySelectorAll('.milestone-tab-btn');
    renderMilestone(fallbackMilestones[0]);

    tabBtns.forEach(btn => {
      btn.addEventListener('click', (e) => {
        tabBtns.forEach(b => b.classList.remove('active'));
        e.currentTarget.classList.add('active');
        const age = e.currentTarget.dataset.age;
        const matched = fallbackMilestones.find(m => m.ageGroup === age) || fallbackMilestones[0];
        renderMilestone(matched);
      });
    });
  }

  function renderMilestone(m) {
    const titleEl = document.getElementById('milestoneTitle');
    const ageLabelEl = document.getElementById('milestoneAgeLabel');
    const iconEl = document.getElementById('milestoneIcon');
    const motorEl = document.getElementById('milestoneMotor');
    const cognitiveEl = document.getElementById('milestoneCognitive');
    const socialEl = document.getElementById('milestoneSocial');
    const redFlagEl = document.getElementById('milestoneRedFlag');
    const parentingEl = document.getElementById('milestoneParenting');

    if (titleEl) titleEl.textContent = m.stageTitle;
    if (ageLabelEl) ageLabelEl.textContent = `Age Group: ${m.ageGroup}`;
    if (iconEl) iconEl.innerHTML = `<i class="fa-solid ${m.icon}"></i>`;
    if (motorEl) motorEl.textContent = m.motorSkills;
    if (cognitiveEl) cognitiveEl.textContent = m.cognitiveSpeech;
    if (socialEl) socialEl.textContent = m.socialEmotional;
    if (redFlagEl) redFlagEl.textContent = m.redFlagSigns;
    if (parentingEl) parentingEl.textContent = m.parentingTips;
  }

  /* ==========================================================================
     2. Pediatric Vaccine Schedule Filter
     ========================================================================== */
  function initVaccineFilter() {
    const filterBtns = document.querySelectorAll('.vaccine-filter-btn');
    renderVaccines(fallbackVaccines);

    filterBtns.forEach(btn => {
      btn.addEventListener('click', (e) => {
        filterBtns.forEach(b => b.classList.remove('active'));
        e.currentTarget.classList.add('active');
        const filter = e.currentTarget.dataset.filter;

        if (filter === 'All') {
          renderVaccines(fallbackVaccines);
        } else if (filter === 'Infant') {
          renderVaccines(fallbackVaccines.filter(v => v.targetAge.includes('Birth') || v.targetAge.includes('Weeks') || v.targetAge.includes('Months')));
        } else if (filter === 'Toddler') {
          renderVaccines(fallbackVaccines.filter(v => v.targetAge.includes('Months') || v.targetAge.includes('Years')));
        } else if (filter === 'School') {
          renderVaccines(fallbackVaccines.filter(v => v.targetAge.includes('Years')));
        }
      });
    });
  }

  function renderVaccines(items) {
    const grid = document.getElementById('vaccineGrid');
    if (!grid) return;

    grid.innerHTML = items.map(v => `
      <div class="vaccine-card">
        <div>
          <div class="vaccine-card-header">
            <h4><i class="fa-solid fa-syringe text-blue"></i> ${v.vaccineName}</h4>
            <span class="vaccine-age-badge">${v.targetAge}</span>
          </div>
          <div class="vaccine-meta">
            <strong><i class="fa-solid fa-shield-virus text-green"></i> Protects Against:</strong> ${v.protectsAgainst}
          </div>
          <p style="font-size: 0.82rem; color: var(--text-muted); line-height: 1.4;">${v.importanceNotes}</p>
        </div>
        <div class="vaccine-tags" style="margin-top: 0.85rem;">
          <span class="vaccine-tag"><i class="fa-solid fa-hashtag"></i> ${v.doseNumber}</span>
          <span class="vaccine-tag"><i class="fa-solid fa-location-crosshairs"></i> ${v.routeOfAdmin}</span>
        </div>
      </div>
    `).join('');
  }

  /* ==========================================================================
     3. Pediatric Fever & Dehydration Triage Tool
     ========================================================================== */
  function initFeverTriage() {
    const calcBtn = document.getElementById('triageCheckBtn');
    if (!calcBtn) return;

    calcBtn.addEventListener('click', () => {
      const ageGroup = document.getElementById('triageAgeSelect').value;
      const tempF = parseFloat(document.getElementById('triageTempInput').value) || 98.6;
      const dehydrationSigns = document.getElementById('triageDehydrationSelect').value;

      evaluatePediatricTriage(ageGroup, tempF, dehydrationSigns);
    });
  }

  function evaluatePediatricTriage(ageGroup, tempF, dehydrationSigns) {
    const statusPill = document.getElementById('triageStatusPill');
    const titleEl = document.getElementById('triageResultTitle');
    const descEl = document.getElementById('triageResultDesc');
    const actionEl = document.getElementById('triageResultAction');

    let severityClass = 'severity-normal';
    let severityText = 'Normal Range';
    let title = 'Temperature is Within Normal Pediatric Range';
    let desc = 'Normal child body temperature fluctuates between 97.7°F and 99.5°F. No medical reduction is required.';
    let action = 'Ensure comfortable clothing, hydration, and regular routine observation.';

    // Immediate Alert 1: Infant under 3 months with fever
    if (ageGroup === 'under3m' && tempF >= 100.4) {
      severityClass = 'severity-emergency';
      severityText = 'EMERGENCY: Immediate Pediatric Care Required';
      title = 'Critical Alert: Infant Under 3 Months with Fever';
      desc = `Temperature of ${tempF}°F in an infant under 3 months can signal a serious neonatal bacterial infection.`;
      action = 'Go directly to the nearest pediatric emergency room or hospital immediately. Do NOT give home medication before clinical assessment.';
    }
    // Immediate Alert 2: High fever >= 104°F at any age or severe dehydration
    else if (tempF >= 104.0 || dehydrationSigns === 'severe') {
      severityClass = 'severity-emergency';
      severityText = 'URGENT MEDICAL ATTENTION';
      title = 'High Fever or Severe Dehydration Detected';
      desc = `Temperature is ${tempF}°F and/or signs of severe dehydration (sunken fontanelle, no tears, lethargy).`;
      action = 'Seek prompt medical evaluation at a healthcare clinic. Offer frequent small sips of ORS or breastmilk on the way.';
    }
    // Moderate: Fever between 101°F - 103.9°F
    else if (tempF >= 101.0) {
      severityClass = 'severity-urgent';
      severityText = 'Moderate Fever';
      title = `Elevated Pediatric Temperature (${tempF}°F)`;
      desc = 'The body is fighting an active infection (often viral cold, flu, or ear infection).';
      action = 'Sponge with lukewarm water (never cold water or ice). Offer plenty of fluids. Consult your pediatrician for weight-accurate paracetamol dosage.';
    }
    // Mild Low Grade: 99.6°F - 100.9°F
    else if (tempF >= 99.6) {
      severityClass = 'severity-mild';
      severityText = 'Mild Low-Grade Temperature';
      title = `Low-Grade Warmth (${tempF}°F)`;
      desc = 'Mild elevation often caused by teething, post-vaccine immune response, or over-bundling.';
      action = 'Dress in single light layer of cotton clothing. Offer breastmilk, water, or light soups. Monitor temperature every 3-4 hours.';
    }

    if (statusPill) {
      statusPill.className = `severity-pill ${severityClass}`;
      statusPill.textContent = severityText;
    }
    if (titleEl) titleEl.textContent = title;
    if (descEl) descEl.textContent = desc;
    if (actionEl) actionEl.innerHTML = `<strong><i class="fa-solid fa-hand-holding-medical text-green"></i> Recommended Action:</strong> ${action}`;
  }

})();
