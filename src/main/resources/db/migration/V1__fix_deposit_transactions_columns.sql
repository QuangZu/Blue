-- Migration script to fix deposit_transactions column types
-- This fixes the issue where qr_code and transaction_reference columns are too small

-- For PostgreSQL
ALTER TABLE deposit_transactions 
ALTER COLUMN qr_code TYPE TEXT;

ALTER TABLE deposit_transactions 
ALTER COLUMN transaction_reference TYPE TEXT;

-- Note: If you're using MySQL, use this instead:
-- ALTER TABLE deposit_transactions MODIFY COLUMN qr_code TEXT;
-- ALTER TABLE deposit_transactions MODIFY COLUMN transaction_reference TEXT;

-- Note: If you're using H2, use this instead:
-- ALTER TABLE deposit_transactions ALTER COLUMN qr_code VARCHAR(65535);
-- ALTER TABLE deposit_transactions ALTER COLUMN transaction_reference VARCHAR(65535);
