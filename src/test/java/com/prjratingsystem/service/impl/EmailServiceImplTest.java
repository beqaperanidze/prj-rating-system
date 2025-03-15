package com.prjratingsystem.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendSellerApprovedEmail_SendsEmail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendSellerApprovedEmail("test@example.com");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendSellerRegistrationEmail_SendsEmailWithConfirmationCode() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendSellerRegistrationEmail("test@example.com", "confirmationCode");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_SendsEmailWithResetCode() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendPasswordResetEmail("test@example.com", "resetCode");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}