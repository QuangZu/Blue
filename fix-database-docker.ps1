# PowerShell script to fix deposit_transactions columns in Docker PostgreSQL

Write-Host "Fixing deposit_transactions table in Docker PostgreSQL..." -ForegroundColor Green

# Execute SQL commands directly in the PostgreSQL container
docker exec -it postgres_blue psql -U blue_r0op_user -d blue_r0op -c "ALTER TABLE deposit_transactions ALTER COLUMN qr_code TYPE TEXT;"

docker exec -it postgres_blue psql -U blue_r0op_user -d blue_r0op -c "ALTER TABLE deposit_transactions ALTER COLUMN transaction_reference TYPE TEXT;"

# Verify the changes
Write-Host "`nVerifying column changes..." -ForegroundColor Yellow
docker exec -it postgres_blue psql -U blue_r0op_user -d blue_r0op -c "SELECT column_name, data_type, character_maximum_length FROM information_schema.columns WHERE table_name = 'deposit_transactions' AND column_name IN ('qr_code', 'transaction_reference');"

Write-Host "`nDatabase fix completed!" -ForegroundColor Green
