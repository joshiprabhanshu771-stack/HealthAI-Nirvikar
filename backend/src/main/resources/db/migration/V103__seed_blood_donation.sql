-- ─────────────────────────────────────────────────────────────────────────────
-- V103__seed_blood_donation.sql
-- Owner : Meenal
-- Purpose: Seed data for Blood Donors, Emergency Blood Requests, and Blood Banks.
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO blood_donors 
(full_name, blood_group, age, gender, city, phone, email, last_donation_date, is_available, total_donations)
VALUES 
('Rohit Sharma', 'O+', 29, 'Male', 'Indore', '+91 98260 11223', 'rohit.sharma@example.com', '2025-11-10', TRUE, 4),
('Ananya Verma', 'A+', 24, 'Female', 'Bhopal', '+91 98930 44556', 'ananya.verma@example.com', '2025-12-05', TRUE, 2),
('Vikramaditya Joshi', 'B+', 32, 'Male', 'Dewas', '+91 94250 77889', 'vikram.joshi@example.com', '2025-10-18', TRUE, 6),
('Pooja Nair', 'O-', 27, 'Female', 'Indore', '+91 97550 22334', 'pooja.nair@example.com', '2025-08-20', TRUE, 3),
('Sameer Khan', 'AB+', 35, 'Male', 'Ujjain', '+91 98270 99881', 'sameer.khan@example.com', '2025-12-28', TRUE, 5),
('Neha Kulkarni', 'A-', 26, 'Female', 'Indore', '+91 98931 66778', 'neha.kulkarni@example.com', '2025-09-15', TRUE, 1),
('Aman Gupta', 'B-', 30, 'Male', 'Bhopal', '+91 94066 33445', 'aman.gupta@example.com', '2025-11-01', TRUE, 3),
('Priya Patidar', 'AB-', 28, 'Female', 'Dewas', '+91 97520 88990', 'priya.patidar@example.com', '2025-07-14', TRUE, 2);

INSERT INTO blood_requests 
(patient_name, blood_group, units_needed, hospital_name, city, contact_person, contact_phone, urgency_level, status, requirement_reason)
VALUES 
('Sunita Devi', 'O-', 2, 'MY Hospital, Indore', 'Indore', 'Dr. Alok Mishra', '+91 98261 00112', 'Critical', 'Open', 'Emergency caesarean section with acute maternal postpartum hemorrhage.'),
('Deepak Malviya', 'B+', 3, 'Bhopal Memorial Hospital', 'Bhopal', 'Rajesh Malviya (Brother)', '+91 98932 11223', 'Urgent', 'Open', 'Undergoing cardiac bypass graft surgery scheduled tomorrow.'),
('Kavita Rathore', 'A+', 1, 'District Civil Hospital, Dewas', 'Dewas', 'Suresh Rathore (Husband)', '+91 94251 44556', 'Critical', 'Open', 'Emergency road traffic trauma and splenic laceration blood loss.'),
('Master Aarav', 'AB+', 2, 'CHL Hospitals, Indore', 'Indore', 'Meena Jain (Mother)', '+91 97551 77889', 'Standard', 'In Progress', 'Scheduled elective pediatric orthopedic bone surgery.');

INSERT INTO blood_banks 
(bank_name, city, address, phone, operating_hours, verified)
VALUES 
('Indore Red Cross Regional Blood Centre', 'Indore', 'Near Collectorate Office, Moti Tabela, Indore, MP', '+91 731 2544108', '24/7 Open', TRUE),
('MY Hospital Model Blood Bank', 'Indore', 'Agra Bombay Rd, Sanyogitaganj, Indore, MP', '+91 731 2527200', '24/7 Open', TRUE),
('Bhopal Red Cross Society Blood Bank', 'Bhopal', 'Red Cross Bhawan, Shivaji Nagar, Bhopal, MP', '+91 755 2551108', '24/7 Open', TRUE),
('Gandhi Medical College Blood Bank', 'Bhopal', 'Sultania Rd, Royal Market, Bhopal, MP', '+91 755 2540590', '24/7 Open', TRUE),
('District Hospital Blood Centre Dewas', 'Dewas', 'Civil Hospital Campus, AB Road, Dewas, MP', '+91 7272 252108', '24/7 Open', TRUE);
