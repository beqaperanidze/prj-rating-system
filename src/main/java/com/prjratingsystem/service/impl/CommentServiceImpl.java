package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.CommentDTO;
import com.prjratingsystem.dto.CommentWithSellerRequestDTO;
import com.prjratingsystem.exception.CommentNotFoundException;
import com.prjratingsystem.exception.UserAlreadyExistException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.Comment;
import com.prjratingsystem.model.enums.Role;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.CommentRepository;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.service.CommentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public CommentDTO createComment(Integer sellerId, CommentDTO commentDTO) {
        User seller = userRepository.findById(sellerId).orElseThrow(() -> new UserNotFoundException("Seller not found with ID: %d".formatted(sellerId)));

        Comment comment = new Comment();
        comment.setMessage(commentDTO.getMessage());
        comment.setUser(seller);
        comment.setApproved(false);

        Comment savedComment = commentRepository.save(comment);
        return mapToCommentDTO(savedComment);
    }

    @Override
    @Transactional
    public CommentDTO createCommentWithSellerRequest(CommentWithSellerRequestDTO requestDTO)
            throws UserAlreadyExistException {
        if (userRepository.existsByEmail(requestDTO.getSellerEmail())) {
            throw new UserAlreadyExistException("User with this email already exists.");
        }

        User seller = new User();
        seller.setFirstName(requestDTO.getSellerFirstName());
        seller.setLastName(requestDTO.getSellerLastName());
        seller.setEmail(requestDTO.getSellerEmail());
        seller.setRole(Role.SELLER);
        seller.setPassword("TEMP_PASSWORD");
        seller.setApproved(false);
        seller = userRepository.save(seller);

        Comment comment = new Comment();
        comment.setMessage(requestDTO.getMessage());
        comment.setUser(seller);
        comment.setApproved(false);

        Comment savedComment = commentRepository.save(comment);
        return mapToCommentDTO(savedComment);
    }


    @Override
    public List<CommentDTO> getCommentsBySellerId(Integer sellerId) {
        userRepository.findById(sellerId).orElseThrow(() -> new UserNotFoundException("Seller not found with ID: %d".formatted(sellerId)));

        return commentRepository.findByUserId(sellerId).stream()
                .map(this::mapToCommentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO getCommentById(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: %d".formatted(commentId)));

        return mapToCommentDTO(comment);
    }

    @Override
    @Transactional
    public CommentDTO updateComment(Integer commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: %d".formatted(commentId)));

        comment.setMessage(commentDTO.getMessage());

        return mapToCommentDTO(comment);

    }

    @Override
    @Transactional
    public void deleteComment(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: %d".formatted(commentId)));

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentDTO approveComment(Integer commentId, boolean approved) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: %d".formatted(commentId)));

        comment.setApproved(approved);
        Comment updatedComment = commentRepository.save(comment);
        return mapToCommentDTO(updatedComment);
    }


    private CommentDTO mapToCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setMessage(comment.getMessage());
        commentDTO.setSellerId(comment.getUser().getId());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setApproved(comment.getApproved());
        return commentDTO;
    }
}
