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

    @Value("beqaperanidzee@gmail.com")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

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
        message.setText("Please click the following link to confirm your registration:\n\n%s/confirm?code=%s".formatted(frontendUrl, confirmationCode));

        mailSender.send(message);
    }
}