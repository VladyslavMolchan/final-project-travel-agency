-- ==============================
-- USERS
-- ==============================
CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,  -- USER / ADMIN
    phone_number VARCHAR(50),
    balance DECIMAL(19,2),
    active BOOLEAN NOT NULL
);

-- ==============================
-- VOUCHERS
-- ==============================
CREATE TABLE voucher (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    tour_type VARCHAR(50),
    transfer_type VARCHAR(50),
    hotel_type VARCHAR(50),
    status VARCHAR(50),
    arrival_date DATE,
    eviction_date DATE,
    user_id UUID,
    hot BOOLEAN NOT NULL,
    CONSTRAINT fk_voucher_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- ==============================
-- ORDERS
-- ==============================
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    voucher_id UUID,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_order_voucher FOREIGN KEY (voucher_id) REFERENCES voucher(id) ON DELETE CASCADE
);

-- ==============================
-- PASSWORD RESET TOKENS
-- ==============================
CREATE TABLE password_reset_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_reset_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);


-- ==============================
-- PASSWORD REFRESH TOKEN
-- ==============================
CREATE TABLE refresh_token (
    id UUID PRIMARY KEY,
    token VARCHAR(500) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    user_id UUID REFERENCES app_user(id)
);

