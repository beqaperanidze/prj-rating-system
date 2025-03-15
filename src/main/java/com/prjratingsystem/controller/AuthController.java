package com.prjratingsystem.controller;

import java.util.Map;

import com.prjratingsystem.exception.UserAlreadyExistsException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.User;
import com.prjratingsystem.service.EmailService;
import com.prjratingsystem.service.PasswordResetService;
import com.prjratingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.prjratingsystem.exception.EmailAlreadyExistsException;
import com.prjratingsystem.exception.UnauthorizedAccessException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;

    @Autowired
    public AuthController(UserService userService, EmailService emailService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        String confirmationCode = userService.registerUser(user);
        emailService.sendSellerRegistrationEmail(user.getEmail(), confirmationCode);

        return ResponseEntity.ok("User registered successfully. Check your email for the confirmation link.");
    }


    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String code) {
        userService.confirmUser(code);
        return ResponseEntity.ok("Email confirmed successfully");
    }


    @PostMapping("/forgot_password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }

        try {
            passwordResetService.generateResetCode(email);
            return ResponseEntity.ok(Map.of("message", "Reset code sent to your email"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check_code")
    public ResponseEntity<?> checkCode(@RequestParam String code) {
        boolean isValid = passwordResetService.validateResetCode(code);
        if (isValid) {
            return ResponseEntity.ok(Map.of("valid", true));
        } else {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String newPassword = request.get("newPassword");

        if (code == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Code and new password are required"));
        }

        boolean result = passwordResetService.resetPassword(code, newPassword);
        if (result) {
            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired reset code"));
        }
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}