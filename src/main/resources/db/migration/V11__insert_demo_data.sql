-- Demo admin user (password: admin123)
INSERT INTO users (username, email, password_hash, first_name, last_name, bio, is_active)
VALUES ('admin', 'admin@skillswap.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Admin', 'User', 'Platform administrator', TRUE);

-- Demo regular user (password: user123)
INSERT INTO users (username, email, password_hash, first_name, last_name, bio, is_active)
VALUES ('johndoe', 'john@example.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'John', 'Doe', 'I love teaching and learning new things!', TRUE);

INSERT INTO users (username, email, password_hash, first_name, last_name, bio, is_active)
VALUES ('janedoe', 'jane@example.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'Jane', 'Doe', 'Full-stack developer and Spanish enthusiast', TRUE);

-- Assign roles
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1); -- admin -> ROLE_USER
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2); -- admin -> ROLE_ADMIN
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1); -- johndoe -> ROLE_USER
INSERT INTO user_roles (user_id, role_id) VALUES (3, 1); -- janedoe -> ROLE_USER

-- Skills
INSERT INTO skills (title, description, category, level) VALUES
('Python Programming', 'Learn Python from basics to advanced', 'PROGRAMMING', 'INTERMEDIATE'),
('Spanish Language', 'Conversational and written Spanish', 'LANGUAGES', 'BEGINNER'),
('Guitar Lessons', 'Acoustic and electric guitar', 'MUSIC', 'ADVANCED'),
('Watercolor Painting', 'Techniques for watercolor art', 'ART', 'INTERMEDIATE'),
('Data Science', 'Machine learning and data analysis', 'PROGRAMMING', 'ADVANCED'),
('French Language', 'French for beginners and intermediate', 'LANGUAGES', 'INTERMEDIATE'),
('Photography', 'Digital photography and editing', 'ART', 'BEGINNER'),
('Yoga', 'Hatha and Vinyasa yoga practice', 'FITNESS', 'INTERMEDIATE');

-- User skills
INSERT INTO user_skills (user_id, skill_id) VALUES (2, 1); -- john knows Python
INSERT INTO user_skills (user_id, skill_id) VALUES (2, 5); -- john knows Data Science
INSERT INTO user_skills (user_id, skill_id) VALUES (3, 2); -- jane knows Spanish
INSERT INTO user_skills (user_id, skill_id) VALUES (3, 6); -- jane knows French

-- Skill offers
INSERT INTO skill_offers (title, description, hours_per_session, hourly_rate, rate_currency, max_students, is_active, owner_id, skill_id) VALUES
('Python for Beginners', 'I will teach you Python programming from scratch. We will cover variables, loops, functions, and OOP.', 1.5, 25.00, 'EUR', 3, TRUE, 2, 1),
('Data Science Masterclass', 'Learn pandas, numpy, scikit-learn and build real ML models.', 2.0, 40.00, 'USD', 2, TRUE, 2, 5),
('Conversational Spanish', 'Practice speaking Spanish with a native speaker. All levels welcome!', 1.0, 20.00, 'EUR', 5, TRUE, 3, 2),
('French for Travelers', 'Essential French phrases and conversation skills for your next trip.', 1.0, 15.00, 'EUR', 4, TRUE, 3, 6);

-- Exchange requests
INSERT INTO exchange_requests (message, status, offer_id, requester_id) VALUES
('I would love to learn Python! I can teach you Spanish in return.', 'ACCEPTED', 1, 3),
('Interested in your Data Science course. I offer French lessons.', 'PENDING', 2, 3);

-- Exchange (for accepted request)
INSERT INTO exchanges (scheduled_at, duration_minutes, notes, completed_at, exchange_request_id) VALUES
('2026-05-15 14:00:00', 90, 'First Python lesson completed successfully', '2026-05-15 15:30:00', 1);

-- Reviews
INSERT INTO reviews (rating, comment, exchange_id, author_id, target_id) VALUES
(5, 'John is an excellent Python teacher! Very patient and clear explanations.', 1, 3, 2),
(4, 'Jane was great to work with. Looking forward to more exchanges!', 1, 2, 3);
