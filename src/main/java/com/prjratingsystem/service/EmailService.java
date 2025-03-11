package com.prjratingsystem.service;

public interface EmailService {

    void sendSellerApprovedEmail(String to);

    void sendSellerRegistrationEmail(String to, String confirmationCode);

}