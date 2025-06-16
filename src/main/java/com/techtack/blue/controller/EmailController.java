package com.techtack.blue.controller;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RestController
public class EmailController {

    private final JavaMailSender mailSender;

    public EmailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RequestMapping("/send-email")
    public String sendEmail() {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("minhquangvuxd@gmail.com");
            helper.setTo("minhquangvuxd@gmail.com");
            helper.setSubject("Simple test email from Me");
            helper.setText("Please find the attached documents below");

            try(var inputStream = Objects.requireNonNull(EmailController.class.getResourceAsStream("/templates/email.html"))){
                helper.setText(
                        new String(inputStream.readAllBytes(), StandardCharsets.UTF_8), true
                );
            }

            mailSender.send(message);
            return "success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
