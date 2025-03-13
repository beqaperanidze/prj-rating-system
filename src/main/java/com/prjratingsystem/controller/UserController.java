package com.prjratingsystem.controller;

import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.dto.UserRegistrationDTO;
import com.prjratingsystem.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        UserDTO user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        List<UserDTO> users = userService.findUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/top")
    public ResponseEntity<List<UserDTO>> getTopSellers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        List<UserDTO> topSellers = userService.getTopSellers(pageable);
        return ResponseEntity.ok(topSellers);
    }

    @GetMapping("/sellers/filter")
    public ResponseEntity<List<UserDTO>> filterSellers(
            @RequestParam(required = false) String gameTitle,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating) {
        List<UserDTO> filteredSellers = userService.filterSellers(gameTitle, minRating, maxRating);
        return ResponseEntity.ok(filteredSellers);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id,
                                              @RequestBody UserRegistrationDTO userRegistrationDTO) {
        UserDTO updatedUser = userService.updateUser(id, userRegistrationDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}