INSERT INTO app_user (id, username, password, email, role, phone_number, balance, active)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'user123',
    '$2a$12$2n5mD9TzMQdb27qZFoEWWORXzD7K1dGghfuPn9b7XcFo4zT2hEKT2', -- Admin@123
    'user123@example.com',
    'ADMIN',
    '1234567890',
    500.50,
    TRUE
);

INSERT INTO app_user (id, username, password, email, role, phone_number, balance, active)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    'user456',
    '$2a$12$rfz9TxY38vLxYwCz2OG.2eP1e39vbJq58nmWrZ9xKQkM4UOTr1hNO', -- User@456
    'user456@example.com',
    'USER',
    '0987654321',
    250.00,
    TRUE
);

INSERT INTO app_user (id, username, password, email, role, phone_number, balance, active)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    'employer1',
    '$2a$12$ftMEsUo2FhnPY8USvZ0I/Oy8HfW5TZRKPbkU3zdxW8t0/nBfrtrc2', -- Employer@1
    'employer1@example.com',
    'EMPLOYER',
    '+380501234567',
    0.00,
    TRUE
);


INSERT INTO voucher (id, title, description, price, tour_type, transfer_type, hotel_type, status, arrival_date, eviction_date, user_id, hot) VALUES
(RANDOM_UUID(), 'Beach Holiday Turkey', 'Resort vacation in Side with excursions', 720.00, 'LEISURE', 'PLANE', 'THREE_STARS', 'PAID', '2025-12-01', '2025-12-08', NULL, TRUE),
(RANDOM_UUID(), 'Wellness Retreat', 'Detox program with nutritionist in Slovenia', 890.00, 'HEALTH', 'BUS', 'FOUR_STARS', 'CANCELED', '2025-11-15', '2025-11-22', NULL, TRUE),
(RANDOM_UUID(), 'Adventure in Peru', 'Sacred Valley trekking', 1950.00, 'ADVENTURE', 'PLANE', 'THREE_STARS', 'REGISTERED', '2025-12-10', '2025-12-20', NULL, FALSE),
(RANDOM_UUID(), 'Ski Trip Alps', 'Winter break for families in Austria', 1250.00, 'SPORTS', 'PLANE', 'FOUR_STARS', 'PAID', '2026-01-05', '2026-01-10', NULL, FALSE),
(RANDOM_UUID(), 'City Break Paris', 'Art and wine tour in Montmartre', 870.00, 'CULTURAL', 'TRAIN', 'THREE_STARS', 'CANCELED', '2025-10-25', '2025-10-28', NULL, TRUE),
(RANDOM_UUID(), 'Beach Holiday Turkey', 'All-inclusive Bodrum package', 1020.00, 'LEISURE', 'PLANE', 'FIVE_STARS', 'PAID', '2026-07-01', '2026-07-08', NULL, TRUE),
(RANDOM_UUID(), 'Wellness Retreat', 'Nature escape and spa program', 820.00, 'HEALTH', 'BUS', 'FOUR_STARS', 'REGISTERED', '2026-03-10', '2026-03-17', NULL, TRUE),
(RANDOM_UUID(), 'Beach Holiday Turkey', 'Charming resort in Belek', 940.00, 'LEISURE', 'PLANE', 'FOUR_STARS', 'CANCELED', '2026-08-01', '2026-08-08', NULL, TRUE),
(RANDOM_UUID(), 'Ski Trip Alps', 'Freestyle ski camp', 1350.00, 'SPORTS', 'PLANE', 'THREE_STARS', 'REGISTERED', '2026-01-12', '2026-01-18', NULL, FALSE),
(RANDOM_UUID(), 'City Break Paris', 'Historic landmarks and cuisine', 720.00, 'CULTURAL', 'TRAIN', 'TWO_STARS', 'PAID', '2025-12-20', '2025-12-23', NULL, TRUE),
(RANDOM_UUID(), 'Adventure in Peru', 'Colca Canyon hiking tour', 1850.00, 'ADVENTURE', 'PLANE', 'TWO_STARS', 'PAID', '2026-05-10', '2026-05-18', NULL, FALSE),
(RANDOM_UUID(), 'Wellness Retreat', 'Mountain spa and breathing practices', 870.00, 'HEALTH', 'BUS', 'THREE_STARS', 'CANCELED', '2026-04-01', '2026-04-08', NULL, TRUE),
(RANDOM_UUID(), 'Beach Holiday Turkey', 'Couple’s retreat in Antalya', 980.00, 'LEISURE', 'PLANE', 'FOUR_STARS', 'REGISTERED', '2026-07-15', '2026-07-22', NULL, TRUE),
(RANDOM_UUID(), 'Ski Trip Alps', 'Intensive ski training for teens', 1180.00, 'SPORTS', 'BUS', 'FOUR_STARS', 'PAID', '2026-02-10', '2026-02-15', NULL, FALSE),
(RANDOM_UUID(), 'City Break Paris', 'Fashion tour and shopping experience', 1050.00, 'CULTURAL', 'PLANE', 'FIVE_STARS', 'CANCELED', '2025-12-10', '2025-12-14', NULL, TRUE),
(RANDOM_UUID(), 'Beach Holiday Turkey', 'Resort deal in Kusadasi', 640.00, 'LEISURE', 'BUS', 'TWO_STARS', 'REGISTERED', '2026-08-10', '2026-08-17', NULL, TRUE),
(RANDOM_UUID(), 'Wellness Retreat', 'Eco spa and yoga retreat', 910.00, 'HEALTH', 'BUS', 'FIVE_STARS', 'PAID', '2026-05-01', '2026-05-08', NULL, TRUE),
(RANDOM_UUID(), 'Adventure in Peru', 'Lima city + nature combo tour', 1750.00, 'ADVENTURE', 'PLANE', 'THREE_STARS', 'REGISTERED', '2026-06-01', '2026-06-09', NULL, FALSE),
(RANDOM_UUID(), 'Ski Trip Alps', 'Alpine lodge family stay', 1220.00, 'SPORTS', 'PLANE', 'FOUR_STARS', 'CANCELED', '2026-01-20', '2026-01-26', NULL, FALSE),
(RANDOM_UUID(), 'Beach Holiday Turkey', 'Budget-friendly package in Izmir', 580.00, 'LEISURE', 'BUS', 'ONE_STAR', 'PAID', '2026-07-10', '2026-07-17', NULL, TRUE),
(RANDOM_UUID(), 'Wellness Retreat', 'Holistic healing in lakeside hotel', 940.00, 'HEALTH', 'BUS', 'FOUR_STARS', 'REGISTERED', '2026-04-10', '2026-04-17', NULL, TRUE),
(RANDOM_UUID(), 'City Break Paris', 'Eiffel and Louvre express tour', 620.00, 'CULTURAL', 'TRAIN', 'THREE_STARS', 'REGISTERED', '2025-12-05', '2025-12-08', NULL, TRUE),
(RANDOM_UUID(), 'Beach Holiday Turkey', 'Adults only resort in Fethiye', 1120.00, 'LEISURE', 'PLANE', 'FIVE_STARS', 'PAID', '2026-09-01', '2026-09-08', NULL, TRUE),
(RANDOM_UUID(), 'Ski Trip Alps', 'Snow fun for couples', 990.00, 'SPORTS', 'PLANE', 'THREE_STARS', 'CANCELED', '2026-02-01', '2026-02-06', NULL, FALSE),
(RANDOM_UUID(), 'Wellness Retreat', 'Silent meditation retreat', 800.00, 'HEALTH', 'BUS', 'THREE_STARS', 'PAID', '2026-03-10', '2026-03-17', NULL, TRUE),
(RANDOM_UUID(), 'City Break Paris', 'Weekend of croissants and culture', 700.00, 'CULTURAL', 'PLANE', 'TWO_STARS', 'REGISTERED', '2026-01-10', '2026-01-13', NULL, TRUE),
(RANDOM_UUID(), 'Beach Holiday Turkey', 'Early bird summer promo in Side', 720.00, 'LEISURE', 'PLANE', 'THREE_STARS', 'PAID', '2026-06-01', '2026-06-08', NULL, TRUE),
(RANDOM_UUID(), 'Adventure in Peru', 'Peru discovery for photographers', 2100.00, 'ADVENTURE', 'PLANE', 'FOUR_STARS', 'CANCELED', '2026-06-15', '2026-06-25', NULL, FALSE),
(RANDOM_UUID(), 'Wellness Retreat', 'Longevity program in Slovenia', 880.00, 'HEALTH', 'BUS', 'FIVE_STARS', 'REGISTERED', '2026-04-01', '2026-04-08', NULL, TRUE),
(RANDOM_UUID(), 'Ski Trip Alps', 'Affordable ski deal in Germany', 950.00, 'SPORTS', 'BUS', 'TWO_STARS', 'PAID', '2026-02-20', '2026-02-25', NULL, FALSE),
(RANDOM_UUID(), 'Beach Holiday Turkey', 'Classic Marmaris hotel + tour pack', 810.00, 'LEISURE', 'BUS', 'THREE_STARS', 'REGISTERED', '2026-08-05', '2026-08-12', NULL, TRUE);



