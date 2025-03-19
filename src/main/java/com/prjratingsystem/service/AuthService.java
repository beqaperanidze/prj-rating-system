package com.prjratingsystem.service;

import com.prjratingsystem.model.User;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthService {
    ResponseEntity<?> login(Map<String, String> request);

    ResponseEntity<String> registerUser(User user);

    ResponseEntity<String> confirmUser(String code);

    ResponseEntity<?> forgotPassword(Map<String, String> request);

    ResponseEntity<?> checkResetCode(String code);

    ResponseEntity<?> resetPassword(Map<String, String> request);
}
