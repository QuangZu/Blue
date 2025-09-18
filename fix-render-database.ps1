# PowerShell script to fix deposit_transactions columns in Render PostgreSQL

$host = "dpg-d2e4fsadbo4c73emk2cg-a.oregon-postgres.render.com"
$port = "5432"
$database = "blue_r0op"
$username = "blue_r0op_user"
$password = "qD6VX9gmwx7OMEqbIzfriJrY85dPuBvq"

Write-Host "Connecting to Render PostgreSQL database..." -ForegroundColor Green

# Set PGPASSWORD environment variable for non-interactive authentication
$env:PGPASSWORD = $password

# Execute SQL commands
Write-Host "Fixing qr_code column..." -ForegroundColor Yellow
psql -h $host -p $port -U $username -d $database -c "ALTER TABLE deposit_transactions ALTER COLUMN qr_code TYPE TEXT;"

Write-Host "Fixing transaction_reference column..." -ForegroundColor Yellow
psql -h $host -p $port -U $username -d $database -c "ALTER TABLE deposit_transactions ALTER COLUMN transaction_reference TYPE TEXT;"

# Verify the changes
Write-Host "`nVerifying column changes..." -ForegroundColor Yellow
psql -h $host -p $port -U $username -d $database -c "SELECT column_name, data_type, character_maximum_length FROM information_schema.columns WHERE table_name = 'deposit_transactions' AND column_name IN ('qr_code', 'transaction_reference');"

# Clear the password from environment
Remove-Item Env:PGPASSWORD

Write-Host "`nDatabase fix completed!" -ForegroundColor Green
Write-Host "Note: You may need to restart your application for changes to take effect." -ForegroundColor Cyan
