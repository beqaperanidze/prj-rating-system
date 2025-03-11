package com.prjratingsystem.controller;

import com.prjratingsystem.model.User;
import com.prjratingsystem.service.EmailService;
import com.prjratingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public AuthController(UserService userService, EmailService emailService, RedisTemplate<String, String> redisTemplate) {
        this.userService = userService;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
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

    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Integer id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        userService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String code) {
        userService.confirmUser(code);
        return ResponseEntity.ok("Email confirmed successfully");
    }
}