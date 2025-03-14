package com.prjratingsystem.service;

public interface EmailService {

    /**
     * Sends an email to notify that a seller has been approved.
     *
     * @param to The recipient's email address
     */
    void sendSellerApprovedEmail(String to);

    /**
     * Sends an email for seller registration with a confirmation code.
     *
     * @param to The recipient's email address
     * @param confirmationCode The confirmation code for registration
     */
    void sendSellerRegistrationEmail(String to, String confirmationCode);

    /**
     * Sends an email to reset the password with a reset code.
     *
     * @param to The recipient's email address
     * @param resetCode The code to reset the password
     */
    void sendPasswordResetEmail(String to, String resetCode);
}