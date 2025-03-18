package com.prjratingsystem.service.impl;

import com.prjratingsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSellerApprovedEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Seller Account Approved");
        message.setText("Your seller account has been approved.");

        mailSender.send(message);
    }


    @Override
    public void sendSellerRegistrationEmail(String to, String confirmationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Confirm Your Seller Registration");

        String confirmationLink = "http://localhost:8080/api/auth/confirm?code=%s".formatted(confirmationCode);

        message.setText("Please click the following link to confirm your registration:\n\n%s".formatted(confirmationLink));

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("Your password reset code is: %s\n\nThis code will expire in 30 minutes.".formatted(resetCode));

        mailSender.send(message);
    }
}