# Email Configuration Setup Script for Blue Trading Platform
Write-Host "====================================" -ForegroundColor Cyan
Write-Host "Blue Trading Platform Email Setup" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Choose your email provider:" -ForegroundColor Yellow
Write-Host "1. Gmail (with App Password)"
Write-Host "2. Mailtrap (for testing - recommended)"
Write-Host "3. Custom SMTP"
Write-Host ""

$choice = Read-Host "Enter your choice (1-3)"

switch ($choice) {
    "1" {
        Write-Host ""
        Write-Host "Gmail Setup Instructions:" -ForegroundColor Green
        Write-Host "1. Enable 2-Factor Authentication on your Gmail account"
        Write-Host "2. Go to: https://myaccount.google.com/apppasswords"
        Write-Host "3. Generate an app password for 'Mail'"
        Write-Host ""
        
        $email = Read-Host "Enter your Gmail address"
        $appPassword = Read-Host "Enter your 16-character App Password (xxxx-xxxx-xxxx-xxxx)"
        
        # Remove spaces from app password
        $appPassword = $appPassword -replace '\s', ''
        
        # Set environment variables
        $env:SPRING_MAIL_HOST = "smtp.gmail.com"
        $env:SPRING_MAIL_PORT = "587"
        $env:SPRING_MAIL_USERNAME = $email
        $env:SPRING_MAIL_PASSWORD = $appPassword
        $env:MAIL_FROM_EMAIL = $email
        $env:MAIL_FROM_NAME = "Blue Trading Platform"
        
        Write-Host ""
        Write-Host "Gmail configuration set!" -ForegroundColor Green
    }
    "2" {
        Write-Host ""
        Write-Host "Mailtrap Setup Instructions:" -ForegroundColor Green
        Write-Host "1. Sign up for free at: https://mailtrap.io"
        Write-Host "2. Go to Email Testing > Inboxes"
        Write-Host "3. Click on your inbox and find SMTP credentials"
        Write-Host ""
        Write-Host "Or use these test credentials (temporary):" -ForegroundColor Yellow
        Write-Host ""
        
        $useTest = Read-Host "Use temporary test account? (y/n)"
        
        if ($useTest -eq "y") {
            # Set test environment variables
            $env:SPRING_MAIL_HOST = "sandbox.smtp.mailtrap.io"
            $env:SPRING_MAIL_PORT = "2525"
            $env:SPRING_MAIL_USERNAME = "test"
            $env:SPRING_MAIL_PASSWORD = "test"
            $env:MAIL_FROM_EMAIL = "noreply@bluetrading.com"
            $env:MAIL_FROM_NAME = "Blue Trading Platform"
            
            Write-Host "Test configuration set!" -ForegroundColor Green
            Write-Host "Note: This is for testing only. Emails won't be delivered." -ForegroundColor Yellow
        } else {
            $username = Read-Host "Enter your Mailtrap username"
            $password = Read-Host "Enter your Mailtrap password"
            
            $env:SPRING_MAIL_HOST = "sandbox.smtp.mailtrap.io"
            $env:SPRING_MAIL_PORT = "2525"
            $env:SPRING_MAIL_USERNAME = $username
            $env:SPRING_MAIL_PASSWORD = $password
            $env:MAIL_FROM_EMAIL = "noreply@bluetrading.com"
            $env:MAIL_FROM_NAME = "Blue Trading Platform"
            
            Write-Host "Mailtrap configuration set!" -ForegroundColor Green
        }
    }
    "3" {
        Write-Host ""
        Write-Host "Custom SMTP Setup:" -ForegroundColor Green
        
        $host = Read-Host "Enter SMTP host"
        $port = Read-Host "Enter SMTP port"
        $username = Read-Host "Enter SMTP username"
        $password = Read-Host "Enter SMTP password"
        $fromEmail = Read-Host "Enter sender email address"
        
        $env:SPRING_MAIL_HOST = $host
        $env:SPRING_MAIL_PORT = $port
        $env:SPRING_MAIL_USERNAME = $username
        $env:SPRING_MAIL_PASSWORD = $password
        $env:MAIL_FROM_EMAIL = $fromEmail
        $env:MAIL_FROM_NAME = "Blue Trading Platform"
        
        Write-Host "Custom SMTP configuration set!" -ForegroundColor Green
    }
    default {
        Write-Host "Invalid choice!" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "Configuration Summary:" -ForegroundColor Cyan
Write-Host "SPRING_MAIL_HOST: $env:SPRING_MAIL_HOST"
Write-Host "SPRING_MAIL_PORT: $env:SPRING_MAIL_PORT"
Write-Host "SPRING_MAIL_USERNAME: $env:SPRING_MAIL_USERNAME"
Write-Host "MAIL_FROM_EMAIL: $env:MAIL_FROM_EMAIL"
Write-Host ""

Write-Host "To start the application with this configuration, run:" -ForegroundColor Yellow
Write-Host "mvn spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "Or if you're using the JAR file:" -ForegroundColor Yellow
Write-Host "java -jar target/blue-*.jar" -ForegroundColor White
Write-Host ""

$testNow = Read-Host "Would you like to start the application now? (y/n)"
if ($testNow -eq "y") {
    Write-Host "Starting application..." -ForegroundColor Green
    mvn spring-boot:run
}
