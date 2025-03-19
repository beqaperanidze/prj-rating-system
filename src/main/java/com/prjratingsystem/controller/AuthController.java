package com.prjratingsystem.controller;

import com.prjratingsystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> user) {
        return authService.register(user);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam String code) {
        return authService.confirmUser(code);
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        return authService.forgotPassword(request);
    }

    @GetMapping("/check_code")
    public ResponseEntity<?> checkCode(@RequestParam String code) {
        return authService.checkResetCode(code);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        return authService.resetPassword(request);
    }
}
