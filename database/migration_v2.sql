USE bank_loan_db;

-- MySQL 8.0.29+ supports ADD COLUMN IF NOT EXISTS.
ALTER TABLE loans ADD COLUMN IF NOT EXISTS next_due_date DATE NULL;

-- Existing approved loans receive their first due date one month after migration.
UPDATE loans
SET next_due_date = DATE_ADD(CURRENT_DATE, INTERVAL 1 MONTH)
WHERE status = 'APPROVED' AND next_due_date IS NULL;
