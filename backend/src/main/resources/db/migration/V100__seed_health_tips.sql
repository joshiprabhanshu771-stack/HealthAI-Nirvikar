-- ─────────────────────────────────────────────────────────────────────────────
-- V100__seed_health_tips.sql
-- Owner : Meenal
-- Purpose: Initial seed data for health_tips table.
-- Source: database/seed.sql
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO health_tips (title, category, icon, short_description, description, why_it_matters, actionable_tip, important_considerations, visual_type, visual_data, keywords, source_name, source_url)
SELECT * FROM (SELECT
    'How Much Fluid Do You Need?',
    'Hydration',
    'fa-glass-water',
    'Learn about general daily hydration recommendations based on age groups and personal circumstances.',
    'Fluid needs vary significantly from person to person. Water is essential for cellular metabolic processes, regulating core body temperature, keeping joints lubricated, and delivering vital nutrients throughout the human body.',
    'Mild dehydration can lead to impaired concentration, headaches, daytime fatigue, reduced physical endurance, and kidney strain.',
    'Sip fluids consistently throughout the day rather than drinking large quantities all at once.',
    'Individuals with heart or kidney conditions should follow physician-specific fluid guidance.',
    'age_table',
    '{"title":"Age-Based Daily Fluid Guidance","note":"Approximate glasses are based on a standard 250 mL glass.","headers":["Age Group","Guide","Glasses"],"rows":[["Adults (Women)","~1.6–2.0+ L/day","~7–8+"],["Adults (Men)","~2.0–2.5+ L/day","~8–10+"]] }',
    'water, hydration, fluid, everyday health',
    'WHO & NHS',
    'https://www.nhs.uk/live-well/eat-well/food-guidelines-and-food-labels/water-drinks-nutrition/'
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM health_tips WHERE title = 'How Much Fluid Do You Need?'
);
