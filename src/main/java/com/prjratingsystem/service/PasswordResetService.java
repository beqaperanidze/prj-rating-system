package com.prjratingsystem.service;

public interface PasswordResetService {

    /**
     * Generates a reset code for the given email address.
     *
     * @param email The email address to generate the reset code for
     */
    void generateResetCode(String email);

    /**
     * Validates the provided reset code.
     *
     * @param code The reset code to validate
     * @return true if the reset code is valid, false otherwise
     */
    boolean validateResetCode(String code);

    /**
     * Resets the password using the provided reset code and new password.
     *
     * @param code The reset code to use for resetting the password
     * @param newPassword The new password to set
     * @return true if the password was successfully reset, false otherwise
     */
    boolean resetPassword(String code, String newPassword);
}