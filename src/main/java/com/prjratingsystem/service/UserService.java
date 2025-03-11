package com.prjratingsystem.service;

import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.dto.UserRegistrationDTO;
import com.prjratingsystem.exception.EmailAlreadyExistsException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.User;

import java.util.List;

public interface UserService {
    /**
     * Registers a new user in the system.
     *
     * @param user The user entity to register
     * @return The registered user entity
     */
    User registerUser(User user);

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


    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for
     * @return The user entity with the given email
     */
    User findByEmail(String email);

    /**
     * Confirms a user based on the provided confirmation code.
     *
     * @param confirmationCode The code used to confirm the user
     */
    void confirmUser(String confirmationCode);

    /**
     * Changes the password for a user.
     *
     * @param userId      The unique identifier of the user
     * @param oldPassword The current password of the user
     * @param newPassword The new password to set for the user
     */
    void changePassword(Integer userId, String oldPassword, String newPassword);

}
