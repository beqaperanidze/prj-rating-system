package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.Role;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDTO> getPendingSellers() {
        return userRepository.findByRoleAndApprovedFalse(Role.SELLER).stream().map(this::mapToUserDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveSeller(Integer sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new UserNotFoundException("Seller not found with ID: %d".formatted(sellerId)));
        seller.setApproved(true);
        userRepository.save(seller);
    }

    @Override
    @Transactional
    public void declineSeller(Integer sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new UserNotFoundException("Seller not found with ID: %d".formatted(sellerId)));
        userRepository.delete(seller);
    }

    private UserDTO mapToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setApproved(user.getApproved());
        return dto;
    }
}