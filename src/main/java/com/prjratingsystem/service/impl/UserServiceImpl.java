package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.dto.UserRegistrationDTO;
import com.prjratingsystem.exception.EmailAlreadyExistsException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.enums.Role;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.CommentRepository;
import com.prjratingsystem.repository.GameObjectRepository;
import com.prjratingsystem.repository.RatingRepository;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.service.EmailService;
import com.prjratingsystem.service.RatingService;
import com.prjratingsystem.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final GameObjectRepository gameObjectRepository;
    private final RatingRepository ratingRepository;
    private final RatingService ratingService;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, CommentRepository commentRepository, RatingRepository ratingRepository, RatingService ratingService, PasswordEncoder passwordEncoder, RedisTemplate<String, String> redisTemplate, GameObjectRepository gameObjectRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.ratingRepository = ratingRepository;
        this.ratingService = ratingService;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.gameObjectRepository = gameObjectRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public String registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRole(Role.SELLER);
        user.setApproved(false);

        String confirmationCode = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(confirmationCode, user.getEmail(), Duration.ofHours(24));

        userRepository.save(user);
        return confirmationCode;
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
        emailService.sendSellerApprovedEmail(user.getEmail());
    }

    @Override
    public UserDTO findUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID: %d".formatted(id)));
        UserDTO userDTO = mapToUserDTO(user);
        if (user.getRole() == Role.SELLER) {
            userDTO.setAverageRating(ratingService.calculateSellerRating(user.getId()));
        }

        return userDTO;
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: %d".formatted(id)));
        ratingRepository.deleteAllByCommentUserId(id);
        commentRepository.deleteAllByUserId(id);
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

    @Override
    public List<UserDTO> getTopSellers(Pageable pageable) {
        Page<User> sellerPage = userRepository.findByRole(Role.SELLER, pageable);
        List<User> sellers = sellerPage.getContent();
        List<UserDTO> sellerDTOs = new ArrayList<>();

        for (User seller : sellers) {
            UserDTO sellerDTO = mapToUserDTO(seller);
            sellerDTO.setAverageRating(ratingService.calculateSellerRating(seller.getId()));
            sellerDTOs.add(sellerDTO);
        }

        sellerDTOs.sort(Comparator.comparingDouble(UserDTO::getAverageRating).reversed());

        return sellerDTOs;
    }

    @Override
    public List<UserDTO> filterSellers(String gameTitle, Double minRating, Double maxRating) {
        List<User> sellers = userRepository.findSellersByGameAndRating(gameTitle, minRating, maxRating);
        return sellers.stream()
                .map(user -> {
                    UserDTO dto = mapToUserDTO(user);
                    dto.setAverageRating(ratingService.calculateSellerRating(user.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /*
    private User populateUserFromDTO(UserRegistrationDTO userRegistrationDTO) {
            User user = new User();
            user.setFirstName(userRegistrationDTO.getFirstName());
            user.setLastName(userRegistrationDTO.getLastName());
            user.setEmail(userRegistrationDTO.getEmail());
            user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
            user.setRole(userRegistrationDTO.getRole());
            user.setApproved(userRegistrationDTO.getApproved());
            return user;
        }
    */
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