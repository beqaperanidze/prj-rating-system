package com.prjratingsystem.service;

public interface PasswordResetService {
    void generateResetCode(String email);
    boolean validateResetCode(String code);
    boolean resetPassword(String code, String newPassword);
}