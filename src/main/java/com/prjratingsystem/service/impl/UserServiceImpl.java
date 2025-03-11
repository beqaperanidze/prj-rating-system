package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.dto.UserRegistrationDTO;
import com.prjratingsystem.exception.EmailAlreadyExistsException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.Role;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.CommentRepository;
import com.prjratingsystem.repository.GameObjectRepository;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.service.GameObjectService;
import com.prjratingsystem.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final GameObjectService gameObjectService;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final GameObjectRepository gameObjectRepository;

    public UserServiceImpl(UserRepository userRepository, CommentRepository commentRepository, GameObjectService gameObjectService, PasswordEncoder passwordEncoder, RedisTemplate<String, String> redisTemplate, GameObjectRepository gameObjectRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.gameObjectService = gameObjectService;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.gameObjectRepository = gameObjectRepository;
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setRole(Role.SELLER);
        user.setApproved(false);
        String confirmationCode = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(user.getEmail(), confirmationCode, Duration.ofHours(24));

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void confirmUser(String confirmationCode) {
        String email = redisTemplate.opsForValue().get(confirmationCode);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: %s".formatted(email)));
        user.setApproved(true);
        userRepository.save(user);
        redisTemplate.delete(confirmationCode);
    }

    @Override
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: %d".formatted(userId)));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    @Override
    public UserDTO findUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID: %d".formatted(id)));
        return mapToUserDTO(user);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream().map(this::mapToUserDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Integer id, UserRegistrationDTO userRegistrationDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID: %d".formatted(id)));

        if (userRegistrationDTO.getFirstName() != null) {
            user.setFirstName(userRegistrationDTO.getFirstName());
        }
        if (userRegistrationDTO.getLastName() != null) {
            user.setLastName(userRegistrationDTO.getLastName());
        }
        if (userRegistrationDTO.getEmail() != null) {
            if (!user.getEmail().equals(userRegistrationDTO.getEmail())) {
                if (existsByEmail(userRegistrationDTO.getEmail())) {
                    throw new EmailAlreadyExistsException("Email is already registered");
                }
                user.setEmail(userRegistrationDTO.getEmail());
            }
        }
        if (userRegistrationDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        }
        if (userRegistrationDTO.getRole() != null) {
            user.setRole(userRegistrationDTO.getRole());
        }

        return mapToUserDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID: %d".formatted(id)));
        commentRepository.deleteAllBySellerId(user);
        gameObjectRepository.deleteAllByUserId(id);
        userRepository.delete(user);
    }

    @Override
    public List<UserDTO> findUsersByRole(String role) {
        Role roleEnum = Role.valueOf(role.toUpperCase());
        return userRepository.findByRole(roleEnum).stream().map(this::mapToUserDTO).collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

//    private User populateUserFromDTO(UserRegistrationDTO userRegistrationDTO) {
//        User user = new User();
//        user.setFirstName(userRegistrationDTO.getFirstName());
//        user.setLastName(userRegistrationDTO.getLastName());
//        user.setEmail(userRegistrationDTO.getEmail());
//        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
//        user.setRole(userRegistrationDTO.getRole());
//        user.setApproved(userRegistrationDTO.getApproved());
//        return user;
//    }

    private UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        userDTO.setApproved(user.getApproved());
        return userDTO;
    }
}