package com.prjratingsystem.service;

import com.prjratingsystem.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthService {

    /**
     * Authenticates a user based on the provided login request.
     *
     * @param request a map containing login credentials (e.g., username and password)
     * @return a ResponseEntity containing the authentication result
     */
    ResponseEntity<?> login(Map<String, String> request);

    /**
     * Registers a new user in the system.
     *
     * @param user the user to be registered
     * @return a ResponseEntity containing the registration result
     */
    ResponseEntity<String> registerUser(User user);

    /**
     * Confirms a user's registration using a confirmation code.
     *
     * @param code the confirmation code
     * @return a ResponseEntity containing the confirmation result
     */
    ResponseEntity<String> confirmUser(String code);

    /**
     * Initiates the password reset process for a user.
     *
     * @param request a map containing the user's email
     * @return a ResponseEntity containing the result of the password reset initiation
     */
    ResponseEntity<?> forgotPassword(Map<String, String> request);

    /**
     * Checks if a provided password reset code is valid.
     *
     * @param code the password reset code
     * @return a ResponseEntity indicating whether the reset code is valid
     */
    ResponseEntity<?> checkResetCode(String code);

    /**
     * Resets a user's password using the provided reset code and new password.
     *
     * @param request a map containing the reset code and new password
     * @return a ResponseEntity containing the result of the password reset
     */
    ResponseEntity<?> resetPassword(Map<String, String> request);
}