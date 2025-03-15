package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.CommentDTO;
import com.prjratingsystem.dto.CommentWithSellerRequestDTO;
import com.prjratingsystem.exception.CommentNotFoundException;
import com.prjratingsystem.exception.UserAlreadyExistsException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.Comment;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.CommentRepository;
import com.prjratingsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createComment_ShouldReturnCommentDTO() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setMessage("Test message");

        User seller = new User();
        seller.setId(1);

        Comment comment = new Comment();
        comment.setMessage("Test message");
        comment.setUser(seller);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(seller));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDTO result = commentService.createComment(1, commentDTO);

        assertNotNull(result);
        assertEquals("Test message", result.getMessage());
        assertEquals(1, result.getSellerId());
    }

    @Test
    void createComment_ShouldThrowUserNotFoundException() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setMessage("Test message");

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> commentService.createComment(1, commentDTO));
    }

    @Test
    void createCommentWithSellerRequest_ShouldReturnCommentDTO() throws UserAlreadyExistsException {
        CommentWithSellerRequestDTO requestDTO = new CommentWithSellerRequestDTO();
        requestDTO.setSellerEmail("test@example.com");
        requestDTO.setSellerFirstName("First");
        requestDTO.setSellerLastName("Last");
        requestDTO.setMessage("Test message");

        User seller = new User();
        seller.setId(1);
        seller.setEmail("test@example.com");

        Comment comment = new Comment();
        comment.setMessage("Test message");
        comment.setUser(seller);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(seller);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDTO result = commentService.createCommentWithSellerRequest(requestDTO);

        assertNotNull(result);
        assertEquals("Test message", result.getMessage());
        assertEquals(1, result.getSellerId());
    }

    @Test
    void createCommentWithSellerRequest_ShouldThrowUserAlreadyExistsException() {
        CommentWithSellerRequestDTO requestDTO = new CommentWithSellerRequestDTO();
        requestDTO.setSellerEmail("test@example.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> commentService.createCommentWithSellerRequest(requestDTO));
    }

    @Test
    void getCommentsBySellerId_ShouldReturnListOfCommentDTOs() {
        User seller = new User();
        seller.setId(1);

        User commentUser = new User();
        commentUser.setId(2);

        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setUser(commentUser);
        Comment comment2 = new Comment();
        comment2.setUser(commentUser);

        comments.add(comment1);
        comments.add(comment2);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(seller));
        when(commentRepository.findByUserId(anyInt())).thenReturn(comments);

        List<CommentDTO> result = commentService.getCommentsBySellerId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getCommentsBySellerId_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> commentService.getCommentsBySellerId(1));
    }

    @Test
    void getCommentById_ShouldReturnCommentDTO() {
        User user = new User();
        user.setId(2);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setMessage("Test message");
        comment.setUser(user);

        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));

        CommentDTO result = commentService.getCommentById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test message", result.getMessage());
    }

    @Test
    void getCommentById_ShouldThrowCommentNotFoundException() {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(1));
    }

    @Test
    void updateComment_ShouldReturnUpdatedCommentDTO() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setMessage("Updated message");

        User user = new User();
        user.setId(2);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setMessage("Test message");
        comment.setUser(user);

        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDTO result = commentService.updateComment(1, commentDTO);

        assertNotNull(result);
        assertEquals("Updated message", result.getMessage());
    }

    @Test
    void updateComment_ShouldThrowCommentNotFoundException() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setMessage("Updated message");

        when(commentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(1, commentDTO));
    }

    @Test
    void deleteComment_ShouldDeleteComment() {
        Comment comment = new Comment();
        comment.setId(1);

        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));

        commentService.deleteComment(1);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_ShouldThrowCommentNotFoundException() {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(1));
    }

    @Test
    void approveComment_ShouldReturnApprovedCommentDTO() {
        User user = new User();
        user.setId(2);

        Comment comment = new Comment();
        comment.setId(1);
        comment.setApproved(false);
        comment.setUser(user);

        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDTO result = commentService.approveComment(1, true);

        assertNotNull(result);
        assertTrue(result.isApproved());
    }

    @Test
    void approveComment_ShouldThrowCommentNotFoundException() {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.approveComment(1, true));
    }
}