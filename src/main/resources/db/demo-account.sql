-- Idempotent demo account data for an existing CareerCompass database.
-- Run after schema.sql and sample-data.sql when you do not want to rebuild the database.

INSERT INTO users (full_name, email, password, phone, state, category, gender, role, enabled)
VALUES ('Demo Student', 'demo@careercompass.com', '$2a$10$pI/9cA06JLVTn9Tq84oXP.sEIqd5h9Gyg94gSLWSJPHE6yEKJpfPy', '9123456780', 'Telangana', 'GENERAL', 'FEMALE', 'STUDENT', TRUE)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    password = VALUES(password),
    phone = VALUES(phone),
    state = VALUES(state),
    category = VALUES(category),
    gender = VALUES(gender),
    role = VALUES(role),
    enabled = VALUES(enabled);

SET @demo_user_id := (SELECT id FROM users WHERE email = 'demo@careercompass.com');

DELETE FROM wishlist WHERE user_id = @demo_user_id;
DELETE FROM prediction_history WHERE user_id = @demo_user_id;

INSERT INTO wishlist (user_id, college_id, branch_id, notes, created_at) VALUES
(@demo_user_id, (SELECT id FROM colleges WHERE name = 'NIT Warangal'), (SELECT id FROM branches WHERE branch_code = 'CSE'), 'Dream JoSAA option for CSE with strong placements.', '2026-05-20 09:00:00'),
(@demo_user_id, (SELECT id FROM colleges WHERE name = 'IIIT Hyderabad'), (SELECT id FROM branches WHERE branch_code = 'AIML'), 'Research-focused AI and ML option in Hyderabad.', '2026-05-20 09:20:00'),
(@demo_user_id, (SELECT id FROM colleges WHERE name = 'JNTUH College of Engineering'), (SELECT id FROM branches WHERE branch_code = 'CSE'), 'Strong home-state backup for TS EAMCET counselling.', '2026-05-20 09:40:00'),
(@demo_user_id, (SELECT id FROM colleges WHERE name = 'AIIMS Delhi'), (SELECT id FROM branches WHERE branch_code = 'MBBS'), 'Medical benchmark college for NEET comparison.', '2026-05-20 10:00:00');

INSERT INTO prediction_history
(user_id, exam_id, `rank`, category, gender, quota, home_state, preferred_branch, preferred_college_type, created_at)
VALUES
(@demo_user_id, (SELECT id FROM exams WHERE exam_type = 'JEE_MAIN'), 27498, 'GENERAL', 'FEMALE', 'HOME_STATE', 'Telangana', 'Computer Science and Engineering', 'NIT', '2026-05-21 10:15:00'),
(@demo_user_id, (SELECT id FROM exams WHERE exam_type = 'JEE_MAIN'), 22100, 'GENERAL', 'FEMALE', 'ALL_INDIA', 'Telangana', 'Electronics and Communication Engineering', 'IIIT', '2026-05-22 11:30:00'),
(@demo_user_id, (SELECT id FROM exams WHERE exam_type = 'TS_EAMCET'), 1417, 'GENERAL', 'FEMALE', 'HOME_STATE', 'Telangana', 'Artificial Intelligence and Machine Learning', 'STATE_COLLEGE', '2026-05-23 14:10:00'),
(@demo_user_id, (SELECT id FROM exams WHERE exam_type = 'NEET_UG'), 1860, 'GENERAL', 'FEMALE', 'ALL_INDIA', 'Telangana', 'MBBS', 'GOVERNMENT_MEDICAL', '2026-05-24 16:45:00'),
(@demo_user_id, (SELECT id FROM exams WHERE exam_type = 'AP_EAMCET'), 3857, 'GENERAL', 'FEMALE', 'HOME_STATE', 'Andhra Pradesh', 'Computer Science and Engineering', 'STATE_COLLEGE', '2026-05-25 12:05:00');
