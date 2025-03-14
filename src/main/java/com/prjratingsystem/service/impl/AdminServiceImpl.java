package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.CommentDTO;
import com.prjratingsystem.dto.RatingDTO;
import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.exception.CommentNotFoundException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.Comment;
import com.prjratingsystem.model.enums.Role;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.CommentRepository;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.service.AdminService;
import com.prjratingsystem.service.RatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final RatingService ratingService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public AdminServiceImpl(UserRepository userRepository, RatingService ratingService, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.ratingService = ratingService;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<UserDTO> getPendingSellers() {
        List<User> pendingSellers = userRepository.findByApprovedFalseAndRole(Role.SELLER);
        return pendingSellers.stream()
                .map(user -> {
                    UserDTO dto = mapToUserDTO(user);
                    dto.setAverageRating(ratingService.calculateSellerRating(user.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
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

    @Override
    public List<CommentDTO> getPendingComments() {
        List<Comment> pendingComments = commentRepository.findByApprovedFalse();

        return pendingComments.stream()
                .map(comment -> {
                    CommentDTO dto = mapToCommentDTO(comment);
                    dto.setSellerId(comment.getUser().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void reviewComment(Integer commentId, boolean approved, Integer ratingValue) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: %d".formatted(commentId)));

        comment.setApproved(approved);
        commentRepository.save(comment);

        if (approved) {
            if (ratingValue == null) {
                throw new IllegalArgumentException("Rating value must be provided when approving a comment.");
            }
            RatingDTO ratingDTO = new RatingDTO();
            ratingDTO.setCommentId(commentId);
            ratingDTO.setRatingValue(ratingValue);
            ratingService.createRating(ratingDTO);
        }
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

    private CommentDTO mapToCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setMessage(comment.getMessage());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setApproved(comment.getApproved());
        return dto;
    }
}