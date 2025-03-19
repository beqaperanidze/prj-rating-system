package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.dto.UserRegistrationDTO;
import com.prjratingsystem.exception.EmailAlreadyExistsException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.User;
import com.prjratingsystem.model.enums.Role;
import com.prjratingsystem.repository.CommentRepository;
import com.prjratingsystem.repository.GameObjectRepository;
import com.prjratingsystem.repository.RatingRepository;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.service.EmailService;
import com.prjratingsystem.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private GameObjectRepository gameObjectRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingService ratingService;


    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void findByEmail_ShouldReturnNull_WhenUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        User result = userService.findByEmail("nonexistent@example.com");

        assertNull(result);
    }

    @Test
    void confirmUser_ShouldApproveUser() {
        User user = new User();
        user.setEmail("test@example.com");

        when(valueOperations.get(anyString())).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(redisTemplate.delete(anyString())).thenReturn(true);
        doNothing().when(emailService).sendSellerApprovedEmail(anyString());

        userService.confirmUser("confirmationCode");

        assertTrue(user.getApproved());
        verify(userRepository).save(user);
        verify(redisTemplate).delete("confirmationCode");
        verify(emailService).sendSellerApprovedEmail("test@example.com");
    }

    @Test
    void confirmUser_ShouldThrowUserNotFoundException() {
        when(valueOperations.get(anyString())).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.confirmUser("confirmationCode"));
    }


    @Test
    void findUserById_ShouldReturnUserDTO() {
        User user = new User();
        user.setId(1);
        user.setRole(Role.SELLER);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(ratingService.calculateSellerRating(anyInt())).thenReturn(4.5);

        UserDTO result = userService.findUserById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(4.5, result.getAverageRating());
    }

    @Test
    void findUserById_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(1));
    }

    @Test
    void findAllUsers_ShouldReturnListOfUserDTOs() {
        User user = new User();
        user.setId(1);

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDTO> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDTO() {
        User user = new User();
        user.setId(1);
        user.setEmail("old@example.com");

        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setEmail("new@example.com");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        UserDTO result = userService.updateUser(1, userRegistrationDTO);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void updateUser_ShouldThrowEmailAlreadyExistsException() {
        User user = new User();
        user.setId(1);
        user.setEmail("old@example.com");

        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setEmail("new@example.com");

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(1, userRegistrationDTO));
    }

    @Test
    void deleteUser_ShouldDeleteUserAndRelatedEntities() {
        User user = new User();
        user.setId(1);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(ratingRepository).deleteAllByCommentUserId(1);
        verify(commentRepository).deleteAllByUserId(1);
        verify(gameObjectRepository).deleteAllByUserId(1);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1));
    }

    @Test
    void findUsersByRole_ShouldReturnListOfUserDTOs() {
        User user = new User();
        user.setId(1);
        user.setRole(Role.SELLER);

        when(userRepository.findByRole(any(Role.class))).thenReturn(Collections.singletonList(user));

        List<UserDTO> result = userService.findUsersByRole("SELLER");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        boolean result = userService.existsByEmail("nonexistent@example.com");

        assertFalse(result);
    }

    @Test
    void getTopSellers_ShouldReturnListOfTopSellerDTOs() {
        User seller = new User();
        seller.setId(1);
        seller.setRole(Role.SELLER);

        Page<User> sellerPage = new PageImpl<>(Collections.singletonList(seller));

        when(userRepository.findByRole(any(Role.class), any(PageRequest.class))).thenReturn(sellerPage);
        when(ratingService.calculateSellerRating(anyInt())).thenReturn(4.5);

        List<UserDTO> result = userService.getTopSellers(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4.5, result.getFirst().getAverageRating());
    }

    @Test
    void filterSellers_ShouldReturnListOfFilteredSellerDTOs() {
        User seller = new User();
        seller.setId(1);
        seller.setRole(Role.SELLER);

        when(userRepository.findSellersByGameAndRating(anyString(), anyDouble(), anyDouble())).thenReturn(Collections.singletonList(seller));
        when(ratingService.calculateSellerRating(anyInt())).thenReturn(4.5);

        List<UserDTO> result = userService.filterSellers("gameTitle", 4.0, 5.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4.5, result.getFirst().getAverageRating());
    }
}