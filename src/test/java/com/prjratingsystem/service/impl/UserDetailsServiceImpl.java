package com.prjratingsystem.service.impl;

import com.prjratingsystem.model.User;
import com.prjratingsystem.model.enums.Role;
import com.prjratingsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;
    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail(testEmail);
        testUser.setPassword(testPassword);
        testUser.setRole(Role.SELLER);
        testUser.setApproved(true);
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExistsAndIsApproved() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        assertNotNull(userDetails);
        assertEquals(testEmail, userDetails.getUsername());
        assertEquals(testPassword, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SELLER")));
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent@example.com")
        );

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetailsWithAdminRole_WhenUserIsAdminAndApproved() {
        User adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword("adminPassword");
        adminUser.setRole(Role.ADMIN);
        adminUser.setApproved(true);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@example.com");

        assertNotNull(userDetails);
        assertEquals("admin@example.com", userDetails.getUsername());
        assertEquals(adminUser.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_ShouldThrowUsernameNotFoundException_WhenUserExistsButIsNotApproved() {
        User unapprovedUser = new User();
        unapprovedUser.setEmail("unapproved@example.com");
        unapprovedUser.setPassword("password");
        unapprovedUser.setRole(Role.SELLER);
        unapprovedUser.setApproved(false);

        when(userRepository.findByEmail("unapproved@example.com")).thenReturn(Optional.of(unapprovedUser));

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unapproved@example.com")
        );

        assertEquals("Your account has not been approved yet.", exception.getMessage());
    }
}