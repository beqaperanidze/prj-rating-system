package com.prjratingsystem.service.impl;

import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    private PasswordResetServiceImpl passwordResetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_CODE = "12345678";
    private static final String TEST_PASSWORD = "newPassword123";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String RESET_CODE_PREFIX = "password_reset:";

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        passwordResetService = new PasswordResetServiceImpl(
                userRepository,
                emailService,
                redisTemplate,
                passwordEncoder
        );
    }

    @Test
    void generateResetCode_ShouldStoreCodeAndSendEmail() {
        User user = new User();
        user.setEmail(TEST_EMAIL);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        passwordResetService.generateResetCode(TEST_EMAIL);

        verify(valueOperations).set(
                argThat(key -> key.startsWith(RESET_CODE_PREFIX)),
                eq(TEST_EMAIL),
                eq(30L),
                eq(TimeUnit.MINUTES)
        );

        verify(emailService).sendPasswordResetEmail(
                eq(TEST_EMAIL),
                argThat(code -> code.length() == 8)
        );
    }

    @Test
    void generateResetCode_WithInvalidEmail_ShouldThrowException() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.generateResetCode(TEST_EMAIL)
        );

        assertEquals("User not found with email: test@example.com", exception.getMessage());
        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    }

    @Test
    void validateResetCode_WithValidCode_ShouldReturnTrue() {
        String key = RESET_CODE_PREFIX + TEST_CODE;
        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean result = passwordResetService.validateResetCode(TEST_CODE);

        assertTrue(result);
    }

    @Test
    void validateResetCode_WithInvalidCode_ShouldReturnFalse() {
        String key = RESET_CODE_PREFIX + TEST_CODE;
        when(redisTemplate.hasKey(key)).thenReturn(false);

        boolean result = passwordResetService.validateResetCode(TEST_CODE);

        assertFalse(result);
    }

    @Test
    void resetPassword_WithValidCode_ShouldUpdatePasswordAndDeleteCode() {
        String key = RESET_CODE_PREFIX + TEST_CODE;
        User user = new User();
        user.setEmail(TEST_EMAIL);

        when(valueOperations.get(key)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);

        boolean result = passwordResetService.resetPassword(TEST_CODE, TEST_PASSWORD);

        assertTrue(result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());

        verify(redisTemplate).delete(key);
    }

    @Test
    void resetPassword_WithInvalidUser_ShouldThrowException() {
        String key = RESET_CODE_PREFIX + TEST_CODE;

        when(valueOperations.get(key)).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword(TEST_CODE, TEST_PASSWORD)
        );

        assertEquals("User not found with email: test@example.com", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void resetPassword_WithNullEmail_ShouldHandleError() {
        String key = RESET_CODE_PREFIX + TEST_CODE;

        when(valueOperations.get(key)).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> passwordResetService.resetPassword(TEST_CODE, TEST_PASSWORD)
        );

        assertTrue(exception.getMessage().contains("null"));
        verify(userRepository, never()).save(any(User.class));
    }
}