package com.prjratingsystem.service;

import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.dto.UserRegistrationDTO;
import com.prjratingsystem.exception.EmailAlreadyExistsException;
import com.prjratingsystem.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    /**
     * Registers a new user in the system.
     *
     * @param userRegistrationDTO The registration data for the new user
     * @return UserDTO containing the registered user's information
     * @throws EmailAlreadyExistsException if the email is already registered
     */
    UserDTO registerUser(UserRegistrationDTO userRegistrationDTO);

    /**
     * Finds a user by their unique identifier.
     *
     * @param id The unique identifier of the user
     * @return UserDTO containing the user's information
     * @throws UserNotFoundException if no user exists with the given id
     */
    UserDTO findUserById(Integer id);

    /**
     * Retrieves all users registered in the system.
     *
     * @return List of UserDTO objects containing all users' information
     */
    List<UserDTO> findAllUsers();

    /**
     * Updates an existing user's information.
     *
     * @param id                  The unique identifier of the user to update
     * @param userRegistrationDTO The updated user information
     * @return UserDTO containing the updated user information
     * @throws UserNotFoundException       if no user exists with the given id
     * @throws EmailAlreadyExistsException if attempting to update to an email that is already in use
     */
    UserDTO updateUser(Integer id, UserRegistrationDTO userRegistrationDTO);

    /**
     * Deletes a user from the system.
     *
     * @param id The unique identifier of the user to delete
     * @throws UserNotFoundException if no user exists with the given id
     */
    void deleteUser(Integer id);

    /**
     * Finds all users with a specific role.
     *
     * @param role The role to filter users by (e.g., "ADMIN", "SELLER")
     * @return List of UserDTO objects containing information of users with the specified role
     */
    List<UserDTO> findUsersByRole(String role);

    /**
     * Checks if a user with the given email already exists in the system.
     *
     * @param email The email to check for existence
     * @return true if the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
