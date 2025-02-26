package com.prjratingsystem.service;

import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.dto.UserRegistrationDTO;

import java.util.List;

public interface UserService {
    UserDTO registerUser(UserRegistrationDTO userRegistrationDTO);

    UserDTO findUserById(Integer id);

    List<UserDTO> findAllUsers();

    UserDTO updateUser(Integer id, UserRegistrationDTO userRegistrationDTO);

    void deleteUser(Integer id);

    List<UserDTO> findUsersByRole(String role);

    boolean existsByEmail(String email);
}