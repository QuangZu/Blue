package com.techtack.blue.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.annotation.PostConstruct;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${application.base.url}")
    private String baseUrl;
    
    @Value("${mail.from.email}")
    private String fromEmail;
    
    @Value("${mail.from.name}")
    private String fromName;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @PostConstruct
    public void validateEmailConfiguration() {
        if (fromEmail == null || fromEmail.isEmpty()) {
            logger.warn("[EMAIL SERVICE] ⚠️ Email service not properly configured!");
            logger.warn("[EMAIL SERVICE] Please configure the following environment variables:");
            logger.warn("[EMAIL SERVICE]   - SPRING_MAIL_USERNAME (your email address)");
            logger.warn("[EMAIL SERVICE]   - SPRING_MAIL_PASSWORD (app password for Gmail, not regular password)");
            logger.warn("[EMAIL SERVICE]   - MAIL_FROM_EMAIL (sender email address)");
            logger.warn("[EMAIL SERVICE] See EMAIL_SETUP_GUIDE.md for detailed instructions.");
        } else {
            logger.info("[EMAIL SERVICE] ✓ Email service configured with sender: {}", fromEmail);
        }
        
        if (baseUrl == null || baseUrl.isEmpty()) {
            logger.warn("[EMAIL SERVICE] ⚠️ Base URL not configured! Using default localhost:8080");
            logger.warn("[EMAIL SERVICE] Please set APPLICATION_BASE_URL environment variable");
        } else {
            logger.info("[EMAIL SERVICE] ✓ Base URL configured: {}", baseUrl);
        }
    }

    public void sendVerificationEmail(String toEmail, String username, String token) {
        try {
            String verificationLink = buildVerificationLink("/auth/verify", "token", token);
            String emailContent = createVerificationEmailContent(verificationLink, username);
            
            boolean emailSent = sendEmailViaSmtp(toEmail, "Please verify your email address", emailContent);
            
            if (emailSent) {
                logger.info("[EMAIL SERVICE] Verification email sent successfully to: {}", toEmail);
            } else {
                throw new RuntimeException("Failed to send verification email");
            }
            
        } catch (Exception e) {
            logger.error("[EMAIL SERVICE] Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
    
    public void sendPasswordResetEmail(String toEmail, String username, String token) {
        try {
            String resetLink = buildVerificationLink("/auth/verify-password-change", "token", token);
            String emailContent = createPasswordResetEmailContent(resetLink, username);
            
            boolean emailSent = sendEmailViaSmtp(toEmail, "Reset Your Password", emailContent);
            
            if (emailSent) {
                logger.info("[EMAIL SERVICE] Password reset email sent successfully to: {}", toEmail);
            } else {
                throw new RuntimeException("Failed to send password reset email");
            }
            
        } catch (Exception e) {
            logger.error("[EMAIL SERVICE] Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
    
    private String buildVerificationLink(String endpoint, String paramName, String paramValue) {
        String effectiveBaseUrl = (baseUrl != null && !baseUrl.isEmpty()) ? baseUrl : "http://localhost:8080";
        return String.format("%s%s?%s=%s", effectiveBaseUrl, endpoint, paramName, paramValue);
    }
    
    /**
     * Handles actual email sending via SMTP
     */
    private boolean sendEmailViaSmtp(String toEmail, String subject, String htmlContent) {
        validateEmailConfiguration();
        
        if (fromEmail == null || fromEmail.isEmpty()) {
            logger.error("[EMAIL SERVICE] Email not configured properly. Please set MAIL_FROM_EMAIL environment variable.");
            throw new RuntimeException("Email service not configured. Please configure SMTP settings.");
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            
            logger.info("[EMAIL SERVICE] Email sent successfully to: {}", toEmail);
            return true;
            
        } catch (MessagingException e) {
            logger.error("[EMAIL SERVICE] MessagingException while sending email to {}: {}", toEmail, e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("Authentication")) {
                logger.error("[EMAIL SERVICE] Authentication failed. Please check your email credentials:");
                logger.error("[EMAIL SERVICE] - For Gmail: Use App Password, not regular password");
                logger.error("[EMAIL SERVICE] - Ensure 2FA is enabled for Gmail");
                logger.error("[EMAIL SERVICE] - Check SPRING_MAIL_USERNAME and SPRING_MAIL_PASSWORD environment variables");
            }
            return false;
        } catch (Exception e) {
            logger.error("[EMAIL SERVICE] Unexpected error sending email to {}: {}", toEmail, e.getMessage());
            logger.error("[EMAIL SERVICE] Error type: {}", e.getClass().getName());
            if (e.getCause() != null) {
                logger.error("[EMAIL SERVICE] Root cause: {}", e.getCause().getMessage());
            }
            return false;
        }
    }
    
    private String createVerificationEmailContent(String verificationLink, String username) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email-verification.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            return template
                .replace("{{verificationLink}}", verificationLink)
                .replace("{{username}}", username != null ? username : "User");
        } catch (IOException e) {
            logger.error("Failed to load email verification template: {}", e.getMessage());
            return String.format(
                "<html><body>" +
                "<h2>Welcome to Blue Trading Platform!</h2>" +
                "<p>Hello %s,</p>" +
                "<p>Please click the link below to verify your email address:</p>" +
                "<p><a href='%s'>Verify Email</a></p>" +
                "<p>If you didn't create this account, please ignore this email.</p>" +
                "</body></html>",
                username != null ? username : "User",
                verificationLink
            );
        }
    }
    
    private String createPasswordResetEmailContent(String resetLink, String username) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/password-reset.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            return template
                .replace("{{resetLink}}", resetLink)
                .replace("{{username}}", username != null ? username : "User");
        } catch (IOException e) {
            logger.error("Failed to load password reset template: {}", e.getMessage());
            return String.format(
                "<html><body>" +
                "<h2>Password Reset Request</h2>" +
                "<p>Hello %s,</p>" +
                "<p>Click the link below to reset your password:</p>" +
                "<p><a href='%s'>Reset Password</a></p>" +
                "<p>If you didn't request this reset, please ignore this email.</p>" +
                "</body></html>",
                username != null ? username : "User",
                resetLink
            );
        }
    }
}