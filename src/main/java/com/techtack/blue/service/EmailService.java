package com.techtack.blue.service;

import org.springframework.stereotype.Service;

/**
 * EmailService - Mail functionality disabled
 * 
 * This service provides email functionality for the application.
 * Currently, mail functionality is disabled
 * and email operations are logged to console instead of being sent.
 */
@Service
public class EmailService {
    
    /**
     * Sends a verification email to the user (currently logs to console)
     * @param to The recipient email address
     * @param verificationUrl The verification URL to include in the email
     */
    public void sendVerificationEmail(String to, String verificationUrl) {
        // Mail functionality disabled - logging instead of sending
        System.out.println("[EMAIL SERVICE] Verification email would be sent to: " + to);
        System.out.println("[EMAIL SERVICE] Verification URL: " + verificationUrl);
        System.out.println("[EMAIL SERVICE] Subject: Please verify your email address");
        System.out.println("[EMAIL SERVICE] Content: Registration verification email with verification link");
    }

    /**
     * Sends a password change verification email to the user (currently logs to console)
     * @param toEmail The recipient email address
     * @param verificationUrl The verification URL to include in the email
     */
    public void sendPasswordChangeVerificationEmail(String toEmail, String verificationUrl) {
        // Mail functionality disabled - logging instead of sending
        System.out.println("[EMAIL SERVICE] Password change verification email would be sent to: " + toEmail);
        System.out.println("[EMAIL SERVICE] Verification URL: " + verificationUrl);
        System.out.println("[EMAIL SERVICE] Subject: Verify Your Password Change");
        System.out.println("[EMAIL SERVICE] Content: Password change verification email with verification link");
    }
}