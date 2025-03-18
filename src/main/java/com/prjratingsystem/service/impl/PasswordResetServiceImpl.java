package com.prjratingsystem.service.impl;

import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.service.EmailService;
import com.prjratingsystem.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private static final String RESET_CODE_PREFIX = "password_reset:";
    private static final long RESET_CODE_EXPIRATION = 30;

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetServiceImpl(
            UserRepository userRepository,
            EmailService emailService,
            RedisTemplate<String, String> redisTemplate,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void generateResetCode(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: %s".formatted(email)));

        String resetCode = UUID.randomUUID().toString().substring(0, 8);

        redisTemplate.opsForValue().set(
                RESET_CODE_PREFIX + resetCode,
                email,
                RESET_CODE_EXPIRATION,
                TimeUnit.MINUTES
        );

        emailService.sendPasswordResetEmail(email, resetCode);
    }

    @Override
    public boolean validateResetCode(String code) {
        String key = RESET_CODE_PREFIX + code;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public boolean resetPassword(String code, String newPassword) {
        String key = RESET_CODE_PREFIX + code;
        String email = redisTemplate.opsForValue().get(key);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: %s".formatted(email)));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redisTemplate.delete(key);

        return true;
    }
}