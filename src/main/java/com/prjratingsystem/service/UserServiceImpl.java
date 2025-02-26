package com.prjratingsystem.service;

import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.dto.UserRegistrationDTO;
import com.prjratingsystem.exception.EmailAlreadyExistsException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.Role;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDTO registerUser(UserRegistrationDTO userRegistrationDTO) {
        if (existsByEmail(userRegistrationDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        User user = populateUserFromDTO(userRegistrationDTO);

        User savedUser = userRepository.save(user);

        return mapToUserDTO(savedUser);
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

    private User populateUserFromDTO(UserRegistrationDTO userRegistrationDTO) {
        User user = new User();
        user.setFirstName(userRegistrationDTO.getFirstName());
        user.setLastName(userRegistrationDTO.getLastName());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        user.setRole(userRegistrationDTO.getRole());
        return user;
    }

    private UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        return userDTO;
    }
}