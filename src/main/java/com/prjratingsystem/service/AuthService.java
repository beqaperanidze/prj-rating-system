package com.prjratingsystem.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthService {
    ResponseEntity<?> login(Map<String, String> request);

    ResponseEntity<String> register(Map<String, String> request);

    ResponseEntity<String> confirmUser(String code);

    ResponseEntity<?> forgotPassword(Map<String, String> request);

    ResponseEntity<?> checkResetCode(String code);

    ResponseEntity<?> resetPassword(Map<String, String> request);
}
