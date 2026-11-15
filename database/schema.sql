CREATE DATABASE IF NOT EXISTS bank_loan_db;
USE bank_loan_db;

CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS loans (
    loan_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    loan_type ENUM('HOME','CAR','EDUCATION') NOT NULL,
    principal DECIMAL(14,2) NOT NULL,
    annual_interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INT NOT NULL,
    status ENUM('PENDING','APPROVED','REJECTED','CLOSED') NOT NULL DEFAULT 'PENDING',
    applied_on DATE NOT NULL,
    next_due_date DATE NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CHECK (principal > 0),
    CHECK (annual_interest_rate >= 0),
    CHECK (tenure_months > 0)
);

CREATE TABLE IF NOT EXISTS repayments (
    repayment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    loan_id BIGINT NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    payment_date DATE NOT NULL,
    note VARCHAR(255),
    FOREIGN KEY (loan_id) REFERENCES loans(loan_id),
    CHECK (amount > 0)
);

DELIMITER //
DROP PROCEDURE IF EXISTS generate_emi_schedule//
CREATE PROCEDURE generate_emi_schedule(IN p_loan_id BIGINT)
BEGIN
    SELECT l.loan_id, l.principal, l.annual_interest_rate, l.tenure_months,
           CASE WHEN l.annual_interest_rate = 0
                THEN ROUND(l.principal / l.tenure_months, 2)
                ELSE ROUND(
                    l.principal * (l.annual_interest_rate / 1200) *
                    POW(1 + l.annual_interest_rate / 1200, l.tenure_months) /
                    (POW(1 + l.annual_interest_rate / 1200, l.tenure_months) - 1), 2)
           END AS monthly_emi
    FROM loans l
    WHERE l.loan_id = p_loan_id AND l.status = 'APPROVED';
END//
DELIMITER ;

-- For an existing database, run once:
-- ALTER TABLE loans ADD COLUMN next_due_date DATE NULL;