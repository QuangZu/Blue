-- PostgreSQL initialization script
-- This will run automatically when the PostgreSQL container is created/recreated

-- Check if the table exists first
DO $$ 
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'deposit_transactions') THEN
        -- Fix qr_code column
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'deposit_transactions' 
            AND column_name = 'qr_code' 
            AND data_type = 'character varying'
        ) THEN
            ALTER TABLE deposit_transactions ALTER COLUMN qr_code TYPE TEXT;
            RAISE NOTICE 'Changed qr_code column to TEXT';
        END IF;

        -- Fix transaction_reference column
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'deposit_transactions' 
            AND column_name = 'transaction_reference' 
            AND data_type = 'character varying'
        ) THEN
            ALTER TABLE deposit_transactions ALTER COLUMN transaction_reference TYPE TEXT;
            RAISE NOTICE 'Changed transaction_reference column to TEXT';
        END IF;
    END IF;
END $$;
