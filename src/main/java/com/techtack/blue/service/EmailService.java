package com.techtack.blue.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    public void sendVerificationEmail(String to, String verificationUrl) {
        System.out.println("[EMAIL SERVICE] Verification email would be sent to: " + to);
        System.out.println("[EMAIL SERVICE] Verification URL: " + verificationUrl);
        System.out.println("[EMAIL SERVICE] Subject: Please verify your email address");
        System.out.println("[EMAIL SERVICE] Content: Registration verification email with verification link");
    }

    public void sendPasswordChangeVerificationEmail(String toEmail, String verificationUrl) {
        // Mail functionality disabled - logging instead of sending
        System.out.println("[EMAIL SERVICE] Password change verification email would be sent to: " + toEmail);
        System.out.println("[EMAIL SERVICE] Verification URL: " + verificationUrl);
        System.out.println("[EMAIL SERVICE] Subject: Verify Your Password Change");
        System.out.println("[EMAIL SERVICE] Content: Password change verification email with verification link");
    }
}