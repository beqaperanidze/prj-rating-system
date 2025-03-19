package com.prjratingsystem.service.impl;

import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.security.JwtUtil;
import com.prjratingsystem.service.AuthService;
import com.prjratingsystem.service.EmailService;
import com.prjratingsystem.service.PasswordResetService;
import com.prjratingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordResetService passwordResetService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, UserService userService, EmailService emailService,
                           PasswordResetService passwordResetService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.passwordResetService = passwordResetService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseEntity<?> login(Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }

        if (!user.getApproved()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Your account is not approved"));
        }

        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @Override
    public ResponseEntity<String> register(Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String firstName = request.get("firstName");
        String lastName = request.get("lastName");


        if (userService.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setApproved(false);

        String confirmationCode = userService.registerUser(user);
        emailService.sendSellerRegistrationEmail(email, confirmationCode);

        return ResponseEntity.ok("User registered successfully. Check your email for the confirmation link.");
    }

    @Override
    public ResponseEntity<String> confirmUser(String code) {
        userService.confirmUser(code);
        return ResponseEntity.ok("Email confirmed successfully");
    }

    @Override
    public ResponseEntity<?> forgotPassword(Map<String, String> request) {
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

    @Override
    public ResponseEntity<?> checkResetCode(String code) {
        boolean isValid = passwordResetService.validateResetCode(code);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    @Override
    public ResponseEntity<?> resetPassword(Map<String, String> request) {
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
}
