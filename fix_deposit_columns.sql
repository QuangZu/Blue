-- Emergency fix for deposit_transactions table
-- Run this script immediately on your PostgreSQL database

-- Check current column types first
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'deposit_transactions' 
AND column_name IN ('qr_code', 'transaction_reference');

-- Fix the columns to use TEXT type which can store unlimited length
ALTER TABLE deposit_transactions 
ALTER COLUMN qr_code TYPE TEXT;

ALTER TABLE deposit_transactions 
ALTER COLUMN transaction_reference TYPE TEXT;

-- Verify the changes
SELECT column_name, data_type, character_maximum_length 
FROM information_schema.columns 
WHERE table_name = 'deposit_transactions' 
AND column_name IN ('qr_code', 'transaction_reference');

-- The character_maximum_length should now be NULL for TEXT columns
