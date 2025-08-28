package com.techtack.blue.service;

import com.google.api.client.util.Value;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String CONTINUE_URL = "https://your-app-domain.com/verify-email";
    
    // Email provider configuration
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;
    
    @Value("${sendgrid.from.email}")
    private String fromEmail;
    
    @Value("${sendgrid.from.name}")
    private String fromName;
    
    public void sendVerificationEmail(String to, String username) {
        try {
            ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                .setUrl(CONTINUE_URL)
                .setHandleCodeInApp(false)
                .build();
            
            String verificationLink = FirebaseAuth.getInstance()
                .generateEmailVerificationLink(to, actionCodeSettings);
            
            String emailContent = createVerificationEmailContent(verificationLink, username);
            
            boolean emailSent = sendEmailViaSendGrid(to, "Please verify your email address", emailContent);
            
            if (emailSent) {
                logger.info("[EMAIL SERVICE] Verification email sent successfully to: {}", to);
            } else {
                throw new RuntimeException("Failed to send verification email");
            }
            
        } catch (FirebaseAuthException e) {
            logger.error("[EMAIL SERVICE] Failed to generate verification link for {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to generate email verification link", e);
        }
    }

    public void sendPasswordChangeVerificationEmail(String toEmail, String username) {
        try {
            ActionCodeSettings actionCodeSettings = ActionCodeSettings.builder()
                .setUrl(CONTINUE_URL)
                .setHandleCodeInApp(false)
                .build();
            
            String passwordResetLink = FirebaseAuth.getInstance()
                .generatePasswordResetLink(toEmail, actionCodeSettings);
            
            String emailContent = createPasswordChangeEmailContent(passwordResetLink, username);
            
            boolean emailSent = sendEmailViaSendGrid(toEmail, "Reset Your Password", emailContent);
            
            if (emailSent) {
                logger.info("[EMAIL SERVICE] Password reset email sent successfully to: {}", toEmail);
            } else {
                throw new RuntimeException("Failed to send password reset email");
            }
            
        } catch (FirebaseAuthException e) {
            logger.error("[EMAIL SERVICE] Failed to generate password reset link for {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to generate password reset link", e);
        }
    }
    
    // Private method to handle actual email sending
    private boolean sendEmailViaSendGrid(String toEmail, String subject, String htmlContent) {
        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, to, content);
            
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("[EMAIL SERVICE] Email sent successfully to: {}", toEmail);
                return true;
            } else {
                logger.error("[EMAIL SERVICE] Failed to send email. Status: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
                return false;
            }
            
        } catch (IOException e) {
            logger.error("[EMAIL SERVICE] Error sending email to {}: {}", toEmail, e.getMessage());
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
                "</body></html>",
                username != null ? username : "User",
                verificationLink
            );
        }
    }
    
    private String createPasswordChangeEmailContent(String passwordResetLink, String username) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/password-reset.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            return template
                .replace("{{resetLink}}", passwordResetLink)
                .replace("{{username}}", username != null ? username : "User");
        } catch (IOException e) {
            logger.error("Failed to load password reset template: {}", e.getMessage());
            return String.format(
                "<html><body>" +
                "<h2>Password Reset Request</h2>" +
                "<p>Hello %s,</p>" +
                "<p>Click the link below to reset your password:</p>" +
                "<p><a href='%s'>Reset Password</a></p>" +
                "</body></html>",
                username != null ? username : "User",
                passwordResetLink
            );
        }
    }
}