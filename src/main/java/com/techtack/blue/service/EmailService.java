package com.techtack.blue.service;

// Temporarily commented out Firebase imports due to dependency issues
/*
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
*/
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String CONTINUE_URL = "https://your-app-domain.com/verify-email";
    
    public void sendVerificationEmail(String to, String verificationUrl) {
        // Temporarily using provided verificationUrl instead of Firebase link
        String verificationLink = verificationUrl != null ? verificationUrl : "#";
        
        // Create email content with verification link
        String emailContent = createVerificationEmailContent(verificationLink);
        
        // For now, log the email content (you can integrate with actual email service later)
        logger.info("[EMAIL SERVICE] Verification email for: {}", to);
        logger.info("[EMAIL SERVICE] Verification Link: {}", verificationLink);
        logger.info("[EMAIL SERVICE] Email Content: {}", emailContent);
        
        // TODO: Integrate with actual email service (SendGrid, AWS SES, etc.)
        // sendActualEmail(to, "Please verify your email address", emailContent);
        
        /* Firebase implementation - uncomment when dependency is available
        try {
            ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                .setUrl(CONTINUE_URL)
                .setHandleCodeInApp(false)
                .build();
            
            String firebaseVerificationLink = FirebaseAuth.getInstance()
                .generateEmailVerificationLink(to, actionCodeSettings);
            
            logger.info("[EMAIL SERVICE] Firebase Verification Link: {}", firebaseVerificationLink);
            
        } catch (FirebaseAuthException e) {
            logger.error("[EMAIL SERVICE] Failed to generate verification link for {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to generate email verification link", e);
        }
        */
    }

    public void sendPasswordChangeVerificationEmail(String toEmail, String verificationUrl) {
        // Temporarily using provided verificationUrl instead of Firebase link
        String passwordResetLink = verificationUrl != null ? verificationUrl : "#";
        
        // Create email content with password reset link
        String emailContent = createPasswordChangeEmailContent(passwordResetLink);
        
        // For now, log the email content
        logger.info("[EMAIL SERVICE] Password change verification email for: {}", toEmail);
        logger.info("[EMAIL SERVICE] Password Reset Link: {}", passwordResetLink);
        logger.info("[EMAIL SERVICE] Email Content: {}", emailContent);
        
        // TODO: Integrate with actual email service
        // sendActualEmail(toEmail, "Verify Your Password Change", emailContent);
        
        /* Firebase implementation - uncomment when dependency is available
        try {
            ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                .setUrl(CONTINUE_URL)
                .setHandleCodeInApp(false)
                .build();
            
            String firebasePasswordResetLink = FirebaseAuth.getInstance()
                .generatePasswordResetLink(toEmail, actionCodeSettings);
            
            logger.info("[EMAIL SERVICE] Firebase Password Reset Link: {}", firebasePasswordResetLink);
            
        } catch (FirebaseAuthException e) {
            logger.error("[EMAIL SERVICE] Failed to generate password reset link for {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to generate password reset link", e);
        }
        */
    }
    
    private String createVerificationEmailContent(String verificationLink) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email-verification.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            // Replace placeholders with actual values
            return template
                .replace("{{verificationLink}}", verificationLink)
                .replace("{{username}}", "User"); // Default username, can be parameterized later
        } catch (IOException e) {
            logger.error("Failed to load email verification template: {}", e.getMessage());
            // Fallback to simple HTML
            return String.format(
                "<html><body>" +
                "<h2>Welcome to Blue Trading Platform!</h2>" +
                "<p>Please click the link below to verify your email address:</p>" +
                "<p><a href='%s'>Verify Email</a></p>" +
                "</body></html>",
                verificationLink
            );
        }
    }
    
    private String createPasswordChangeEmailContent(String passwordResetLink) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/password-reset.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            // Replace placeholders with actual values
            return template
                .replace("{{resetLink}}", passwordResetLink)
                .replace("{{username}}", "User"); // Default username, can be parameterized later
        } catch (IOException e) {
            logger.error("Failed to load password reset template: {}", e.getMessage());
            // Fallback to simple HTML
            return String.format(
                "<html><body>" +
                "<h2>Password Reset Request</h2>" +
                "<p>Click the link below to reset your password:</p>" +
                "<p><a href='%s'>Reset Password</a></p>" +
                "</body></html>",
                passwordResetLink
            );
        }
    }
    
    // TODO: Implement actual email sending using your preferred email service
    // private void sendActualEmail(String to, String subject, String content) {
    //     // Integrate with SendGrid, AWS SES, or other email service
    // }
}