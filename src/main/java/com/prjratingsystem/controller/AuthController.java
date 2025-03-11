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
        userService.registerUser(user);

        emailService.sendSellerRegistrationEmail(user.getEmail(), redisTemplate.opsForValue().get(user.getEmail()));

        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String code) {
        userService.confirmUser(code);
        return ResponseEntity.ok("Email confirmed successfully");
    }
}