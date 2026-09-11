-- ─────────────────────────────────────────────────────────────────────────────
-- V101__seed_pregnancy_care.sql
-- Owner : Meenal
-- Purpose: Initial seed data for Pregnancy Trimesters, Prenatal Nutrition, and Warning Signs.
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO pregnancy_trimester_guides 
(trimester_number, trimester_name, week_range, summary, baby_development, mother_changes, key_nutrition_tips, recommended_tests, fruit_size_comparison, icon)
VALUES 
(
    1, 
    'First Trimester', 
    'Weeks 1 - 12', 
    'The foundation phase of pregnancy where all major vital organs and body structures begin to form.',
    'Heart begins beating around week 5-6. Neural tube forms brain and spinal cord. Tiny limb buds grow into arms and legs with distinct fingers and toes.',
    'Hormonal surge (hCG & Progesterone), morning sickness/nausea, heightened sense of smell, breast tenderness, frequent urination, and profound fatigue.',
    '400-600 mcg Folic Acid daily to prevent neural tube defects, vitamin B6 for nausea, gentle hydration, small frequent meals.',
    'Dating Ultrasound (Week 6-8), Nuchal Translucency (NT) Scan (Week 11-13), Complete Blood Count, Blood Group & Rh Factor, Thyroid Profile.',
    'Poppy Seed to Plum',
    'fa-seedling'
),
(
    2, 
    'Second Trimester', 
    'Weeks 13 - 26', 
    'Often referred to as the "Golden Trimester" as nausea fades and energy levels substantially improve.',
    'Baby can hear sounds, suck their thumb, blink, and develop fine hair (lanugo). Fetal movements ("quickening") become noticeable between weeks 18-22.',
    'Baby bump becomes visible, skin pigmentation changes (linea nigra), mild round ligament stretching pains, improved energy and appetite.',
    'High iron intake (27 mg/day) to support increased blood volume, Calcium (1000 mg/day) and Vitamin D for bone mineralisation, Omega-3 (DHA) for brain development.',
    'Comprehensive Level II Anomaly Scan (Week 18-20), Glucose Screening Test (OGTT) for gestational diabetes (Week 24-28).',
    'Lemon to Papaya / Cauliflower',
    'fa-baby'
),
(
    3, 
    'Third Trimester', 
    'Weeks 27 - 40+', 
    'The final preparation phase where the baby rapidly gains weight and prepares for birth.',
    'Lungs mature surfactant production, eyes open and close, bones fully harden, and baby practices breathing motions and positions head down (cephalic).',
    'Shortness of breath, backache, Braxton Hicks practice contractions, frequent urination as baby presses bladder, swollen ankles, nested instincts.',
    'Higher protein requirement (75-100g/day), fiber-rich foods for digestion, continuous hydration, magnesium for muscle cramps.',
    'Growth Ultrasound & Doppler Scan (Week 32-36), Non-Stress Test (NST) if indicated, Group B Strep (GBS) swab (Week 36-37).',
    'Eggplant to Watermelon / Pumpkin',
    'fa-heart-pulse'
);

INSERT INTO pregnancy_nutrition 
(nutrient_name, category, daily_target, why_needed, rich_sources, is_safe, caution_notes, icon)
VALUES 
(
    'Folic Acid (Folate)', 
    'Essential Nutrient', 
    '400 - 600 mcg', 
    'Crucial for early neural tube development; prevents spina bifida and brain defects.',
    'Dark leafy greens (spinach, kale), fortified whole grains, lentils, chickpeas, citrus fruits.',
    TRUE, 
    'Start prior to conception or as soon as pregnancy is confirmed; continue through first trimester.',
    'fa-leaf'
),
(
    'Elemental Iron', 
    'Essential Nutrient', 
    '27 mg', 
    'Powers maternal hemoglobin production to supply oxygen across placenta and prevent anemia.',
    'Spinach, lentils, beans, fortified cereals, lean poultry, dried apricots, pumpkin seeds.',
    TRUE, 
    'Pair with Vitamin C (lemon, oranges) to enhance absorption. Avoid taking iron directly with calcium/tea.',
    'fa-droplet'
),
(
    'Calcium & Vitamin D', 
    'Essential Nutrient', 
    '1000 mg Calcium / 600 IU Vit D', 
    'Builds baby bones, teeth, heart rhythm, and nerve signaling without depleting mother bones.',
    'Pasteurized milk, yogurt, paneer, fortified plant milks, tofu, chia seeds, morning sunlight.',
    TRUE, 
    'Separate calcium supplements by at least 2 hours from iron supplements.',
    'fa-bone'
),
(
    'DHA & Omega-3 Fatty Acids', 
    'Essential Nutrient', 
    '200 - 300 mg DHA', 
    'Supports rapid fetal brain growth, cognitive performance, and retinal eye development.',
    'Walnuts, chia seeds, flaxseed oil, algae supplements, low-mercury cooked fish (salmon).',
    TRUE, 
    'Avoid high-mercury fish such as swordfish, shark, king mackerel, and tilefish.',
    'fa-brain'
),
(
    'Cooked Eggs & Lean Protein', 
    'Safe Food', 
    '75 - 100 g protein', 
    'Provides essential amino acids for tissue and placenta synthesis, plus choline for brain cells.',
    'Hard-boiled eggs, thoroughly cooked lentils, cottage cheese (paneer), roasted chickpeas, chicken breast.',
    TRUE, 
    'Ensure eggs are fully cooked until both yolk and whites are firm. Never eat raw or runny eggs.',
    'fa-egg'
),
(
    'Raw / Unpasteurized Dairy', 
    'Avoid Food', 
    '0 mg (Strict Avoidance)', 
    'Poses severe risk of Listeria monocytogenes infection, which can cause miscarriage or neonatal sepsis.',
    'Unpasteurized raw milk, soft cheeses made with unpasteurized milk (brie, camembert, uncertified feta).',
    FALSE, 
    'Always verify labels state "Made with Pasteurized Milk". Boil milk thoroughly if sourced locally.',
    'fa-ban'
),
(
    'Uncooked / Raw Seafood & Meat', 
    'Avoid Food', 
    '0 mg (Strict Avoidance)', 
    'Can harbor Salmonella, Toxoplasma gondii, and coliform bacteria harmful to fetal health.',
    'Sushi with raw fish, rare steaks, raw batter containing eggs, unwashed raw deli meats.',
    FALSE, 
    'Cook all poultry, meat, and seafood to safe internal temperatures (>165°F / 74°C).',
    'fa-triangle-exclamation'
),
(
    'Excessive Caffeine & Alcohol', 
    'Avoid Food', 
    'Max <200mg Caffeine / 0 Alcohol', 
    'Alcohol causes Fetal Alcohol Spectrum Disorders. Excess caffeine crosses placenta and restricts growth.',
    'Energy drinks, high-caffeine espresso shots, alcoholic beverages, wine, beer.',
    FALSE, 
    'Limit tea/coffee to max 1-2 small cups daily (<200mg total). Zero tolerance for alcohol.',
    'fa-mug-hot'
);

INSERT INTO pregnancy_warning_signs 
(symptom_name, urgency_level, description, possible_causes, action_required, icon)
VALUES 
(
    'Vaginal Bleeding or Spotting', 
    'Immediate Emergency', 
    'Bright red bleeding or heavy spotting accompanied by cramping or passing tissue.',
    'Threatened miscarriage, ectopic pregnancy, placenta previa, placental abruption, or cervix irritation.',
    'Lie down immediately, do not use tampons, and go to the nearest emergency obstetric clinic right away.',
    'fa-droplet'
),
(
    'Severe Headache with Vision Changes', 
    'Urgent Doctor Visit', 
    'Persistent throbbing headache, flashing lights, blurred vision, or sudden severe swelling in hands/face.',
    'Preeclampsia (pregnancy-induced high blood pressure).',
    'Check blood pressure immediately and contact your obstetrician without delay.',
    'fa-eye-slash'
),
(
    'Significant Decrease in Fetal Movement', 
    'Urgent Doctor Visit', 
    'Baby is kicking noticeably less than usual after week 28 (fewer than 10 kicks in 2 hours of quiet rest).',
    'Fetal distress, umbilical cord compression, reduced amniotic fluid.',
    'Drink cold water, lie on your left side, and count kicks for 1 hour. If still low, go to labor & delivery.',
    'fa-baby'
),
(
    'Sudden Fluid Leakage (Water Breaking)', 
    'Immediate Emergency', 
    'Continuous trickle or sudden gush of clear, pinkish, or greenish watery fluid before week 37.',
    'Preterm premature rupture of membranes (PPROM).',
    'Note fluid color/time, wear a clean maternity pad, do not insert anything into vagina, head to hospital.',
    'fa-water'
),
(
    'High Fever & Chills (> 100.4°F / 38°C)', 
    'Urgent Doctor Visit', 
    'Elevated body temperature accompanied by shivering, body ache, or burning urination.',
    'Urinary Tract Infection (UTI), kidney infection (pyelonephritis), or systemic viral infection.',
    'Consult your healthcare provider for safe fever-reducing medicines and targeted urine culture analysis.',
    'fa-temperature-arrow-up'
);
