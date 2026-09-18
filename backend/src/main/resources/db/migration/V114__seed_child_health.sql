-- ─────────────────────────────────────────────────────────────────────────────
-- V102__seed_child_health.sql
-- Owner : Meenal
-- Purpose: Seed data for Child Developmental Milestones, Pediatric Vaccines, and Illness Protocols.
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO child_milestones 
(age_group, stage_title, motor_skills, cognitive_speech, social_emotional, red_flag_signs, parenting_tips, icon)
VALUES 
(
    '0-3 Months',
    'Newborn & Early Infant Reflexes',
    'Lifts head briefly while on tummy; moves both arms and legs equally; opens and shuts hands; strong rooting and grasp reflexes.',
    'Startles at loud sounds; turns head toward maternal voice; coos and makes pleasant gurgling sounds; follows moving faces with eyes.',
    'Develops first spontaneous and responsive social smiles (by 6-8 weeks); calms to gentle touch or rock; establishes eye contact.',
    'Does not react to loud noises; does not follow moving objects; does not smile by 8 weeks; floppy muscle tone or extreme stiffness.',
    'Encourage 3-5 minutes of supervised tummy time daily when awake; talk and sing gently during diaper changes.',
    'fa-baby'
),
(
    '4-6 Months',
    'Rolling, Reaching & Babbling',
    'Rolls from tummy to back; supports weight on legs when held upright; pushes up on elbows during tummy time; reaches and grasps toys with both hands.',
    'Babbles with consonants (da-da, ba-ba); laughs out loud; responds to sounds by making sounds; explores objects by bringing them to mouth.',
    'Enjoys playful interaction (peek-a-boo); recognizes familiar family faces; shows excitement by waving arms and kicking legs.',
    'Cannot hold head steady by 4 months; does not reach for objects; does not roll in either direction; shows no affection to caregivers.',
    'Introduce colorful safe rattles; continue exclusive breastfeeding/formula until 6 completed months; talk back when baby babbles.',
    'fa-baby-carriage'
),
(
    '7-12 Months',
    'Sitting, Crawling & First Words',
    'Sits steadily without support; crawls on hands and knees; pulls up to stand while holding furniture; develops pincer grasp (thumb and forefinger).',
    'Responds to own name; understands simple words like "No"; says first meaningful word ("Mama", "Dada", "Ball"); looks at pictures in books.',
    'Shows separation anxiety with strangers; waves "bye-bye" and claps hands; plays interactive games; points to objects of desire.',
    'Does not sit unsupported by 9 months; does not bear weight on legs; does not babble or respond to name; does not point by 12 months.',
    'Childproof the home thoroughly (electrical outlets, sharp corners); introduce single-ingredient soft mashed foods after 6 months.',
    'fa-child'
),
(
    '1-3 Years',
    'Toddler Independence & Language Leap',
    'Walks steadily alone; runs, climbs stairs with support, kicks a ball, stacks 4-6 blocks, scribbles with crayons, drinks from open cup.',
    'Vocabulary grows from 10-20 words at 18m to 200+ words and 2-3 word sentences ("want milk", "big car") by age 2-3; follows 2-step commands.',
    'Shows empathy to crying peers; engages in parallel and simple pretend play; begins toilet training readiness; displays growing independence.',
    'Cannot walk by 18 months; speaks fewer than 6 words by 18 months or no 2-word phrases by 2 years; loses previously acquired skills; lacks eye contact.',
    'Read picture books daily; limit screen time to zero (under 2) or under 1 hr/day; offer healthy finger foods and establish bedtime routines.',
    'fa-child-reaching'
),
(
    '4-6 Years',
    'Preschooler Socialization & Coordination',
    'Hops on one foot; catches a bounced ball; cuts with safety scissors; dresses self independently; draws a person with 4+ body parts.',
    'Speaks clearly in complete sentences; tells stories; understands counting, colors, and basic time concepts (yesterday, today, tomorrow).',
    'Plays cooperatively in groups; shares toys with peers; understands rules in games; displays a wide range of emotions and imagination.',
    'Extremely aggressive or fearful behavior; cannot jump or balance on one foot; unintelligible speech to strangers; cannot copy a circle or cross.',
    'Encourage outdoor active play (60 mins daily); foster social playdates; reinforce handwashing and oral hygiene habits twice daily.',
    'fa-people-group'
),
(
    '7-12 Years',
    'School-Age Mastery & Reasoning',
    'Refined fine motor skills (fluent writing, musical instruments, crafts); advanced athletic coordination and team sports proficiency.',
    'Logical reasoning, problem-solving, reading fluency, understanding abstract concepts and moral fairness.',
    'Forms close peer friendships; understands social norms; develops personal hobbies, self-esteem, and independent decision-making.',
    'Severe academic struggle unresponsive to support; extreme social withdrawal or bullying behavior; persistent sleep disturbances or depression.',
    'Provide balanced nutritious meals with brain-boosting omega-3 & iron; ensure 9-11 hours of sleep; promote open communication about emotions.',
    'fa-graduation-cap'
);

INSERT INTO child_vaccines 
(vaccine_name, target_age, protects_against, dose_number, route_of_admin, importance_notes, mandatory_status)
VALUES 
(
    'BCG (Bacille Calmette-Guérin)',
    'Birth',
    'Tuberculosis (TB) Meningitis & Disseminated TB',
    'Single Dose (0.1 ml)',
    'Intradermal (Left Upper Arm)',
    'Administered immediately after birth before discharge. Causes a small benign scar at injection site.',
    'Universal Essential'
),
(
    'OPV-0 & Hepatitis B-1',
    'Birth',
    'Poliomyelitis & Perinatal Hepatitis B Infection',
    'Birth Dose',
    'Oral Drops (OPV) & Intramuscular (Hep B)',
    'Hep B birth dose must be given within 24 hours of birth to prevent vertical transmission from mother.',
    'Universal Essential'
),
(
    'Pentavalent-1 + IPV-1 + Rota-1 + PCV-1',
    '6 Weeks',
    'Diphtheria, Pertussis, Tetanus, Hep B, Hib, Polio, Rotavirus Diarrhea & Pneumococcal Pneumonia',
    'Primary Dose 1',
    'Intramuscular (Thigh) & Oral Drops (Rota)',
    'Protects against 8 major fatal childhood bacterial and viral infections.',
    'Universal Essential'
),
(
    'Pentavalent-2 + Rota-2',
    '10 Weeks',
    'Diphtheria, Pertussis, Tetanus, Hep B, Hib & Rotavirus',
    'Primary Dose 2',
    'Intramuscular & Oral Drops',
    'Reinforces immune antibodies established during the 6-week immunization.',
    'Universal Essential'
),
(
    'Pentavalent-3 + IPV-2 + Rota-3 + PCV-2',
    '14 Weeks',
    'Diphtheria, Pertussis, Tetanus, Hep B, Hib, Polio, Rotavirus & Pneumococcal',
    'Primary Dose 3',
    'Intramuscular & Oral Drops',
    'Completes the primary infant immunization series for basic pathogens.',
    'Universal Essential'
),
(
    'MR-1 (Measles-Rubella) + PCV Booster + Vit A',
    '9 - 12 Months',
    'Measles, Rubella, Pneumococcal Pneumonia & Vitamin A Deficiency (Night Blindness)',
    '1st Dose MR + Booster PCV',
    'Subcutaneous (Right Arm) & Oral Vitamin A',
    'Crucial for preventing life-threatening measles complications, pneumonia, and blindness.',
    'Universal Essential'
),
(
    'MR-2 + DTP Booster-1 + OPV Booster',
    '16 - 24 Months',
    'Measles, Rubella, Diphtheria, Pertussis, Tetanus & Polio',
    '1st Booster Dose',
    'Intramuscular (Anterolateral Thigh) & Oral Drops',
    'Maintains protective immunity throughout active toddlerhood exploration.',
    'Universal Essential'
),
(
    'DTP Booster-2',
    '5 - 6 Years',
    'Diphtheria, Whooping Cough (Pertussis) & Tetanus',
    '2nd Booster Dose',
    'Intramuscular (Upper Arm Deltoid)',
    'Ensures high-level school-entry immunity against respiratory bacterial toxins.',
    'Universal Essential'
),
(
    'Td (Tetanus & adult Diphtheria) / HPV',
    '10 - 16 Years',
    'Tetanus, Diphtheria & Human Papillomavirus (Cervical Cancer prevention)',
    'Adolescent Dose',
    'Intramuscular (Deltoid)',
    'Administered at age 10 and age 16 for long-term adulthood tetanus protection and HPV oncology defense.',
    'Universal Essential'
);

INSERT INTO child_illness_guides 
(condition_name, category, common_symptoms, home_care_steps, danger_signs, prevention_tips, icon)
VALUES 
(
    'Pediatric Fever (Pyrexia)',
    'Fever Protocol',
    'Body temperature > 100.4°F (38°C), flushed cheeks, warm forehead, irritability, lethargy, decreased appetite.',
    'Keep child comfortably dressed in light cotton clothes; sponge with lukewarm water (never cold or alcohol); offer frequent fluids/breastmilk; give paracetamol as strictly prescribed by pediatrician.',
    'Fever in infants < 3 months; fever > 104°F (40°C); stiff neck; difficulty breathing; seizure/convulsion; non-blanching purple rash.',
    'Maintain timely vaccinations; wash hands before handling infants; avoid crowded places during seasonal viral surges.',
    'fa-temperature-high'
),
(
    'Acute Diarrhea & Vomiting (Gastroenteritis)',
    'Digestive',
    'Watery loose stools (>3 times/day), mild stomach cramping, nausea, vomiting, mild low-grade fever.',
    'Start Oral Rehydration Salts (ORS) immediately after every loose stool; continue regular breastfeeding/soft diet (khichdi, curd, banana); give Zinc drops daily for 14 days as advised.',
    'Sunken eyes/fontanelle; dry tongue with no saliva; absence of tears when crying; no urination for > 6 hours; blood in stool; uncontrollable vomiting.',
    'Wash hands thoroughly with soap before feeding; boil drinking water; ensure Rotavirus vaccination.',
    'fa-toilet-paper'
),
(
    'Common Cold, Cough & Croup',
    'Respiratory',
    'Runny or stuffy nose, sneezing, mild cough, barking seal-like cough in croup, mild throat redness.',
    'Use saline nasal drops before feeding and sleeping to clear nasal passages; elevate head slightly; run cool mist humidifier; offer warm fluids (for children > 1 yr warm water with honey).',
    'Rapid breathing (>50 breaths/min); chest indrawing (ribs sucking in); stridor (harsh whistling sound when inhaling); blue tint around lips (cyanosis); inability to drink.',
    'Avoid exposure to tobacco smoke and dust; practice good respiratory hygiene and handwashing.',
    'fa-lungs'
),
(
    'Ear Infection (Otitis Media)',
    'Ear & Throat',
    'Ear pain, ear pulling/tugging in non-verbal infants, excessive night crying, fluid discharge from ear, temporary muffled hearing.',
    'Apply warm cloth compress over outer ear for soothing comfort; ensure child drinks fluids upright; consult pediatrician for targeted ear evaluation.',
    'Swelling or redness behind the ear (mastoid area); persistent high fever; yellowish foul-smelling pus draining from ear canal; balance issues.',
    'Never feed infant flat on their back (hold at 45° angle); avoid inserting cotton swabs or foreign objects into ear canal; maintain Pneumococcal and Flu vaccines.',
    'fa-ear-listen'
),
(
    'Childhood Rashes (Eczema, Heat Rash, HFM)',
    'Skin Rash',
    'Red itchy patches in skin folds (Eczema), tiny red bumps with prickly sensation (Heat rash), small red blisters on palms, soles & mouth (Hand-Foot-Mouth).',
    'Keep skin cool and dry; use fragrance-free gentle moisturizers on damp skin; dress in loose breathable cotton clothing; keep child nails trimmed short to prevent scratching infection.',
    'Rapidly spreading rash accompanied by high fever; purple or dark red pinprick spots that do not fade when pressed with a glass (petechiae); infected blisters oozing yellow honey-colored pus.',
    'Bathe in lukewarm water; avoid harsh soaps; disinfect shared toys in daycare; keep child hydrated.',
    'fa-hand-dots'
);
