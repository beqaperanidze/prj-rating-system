package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.CommentDTO;
import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.exception.CommentNotFoundException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.Comment;
import com.prjratingsystem.model.User;
import com.prjratingsystem.model.enums.Role;
import com.prjratingsystem.repository.CommentRepository;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPendingSellers_ShouldReturnListOfPendingSellerDTOs() {
        List<User> pendingSellers = new ArrayList<>();
        User seller = new User();
        seller.setId(1);
        seller.setRole(Role.SELLER);
        pendingSellers.add(seller);

        when(userRepository.findByApprovedFalseAndRole(Role.SELLER)).thenReturn(pendingSellers);
        when(ratingService.calculateSellerRating(anyInt())).thenReturn(4.5);

        List<UserDTO> result = adminService.getPendingSellers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4.5, result.get(0).getAverageRating());
    }

    @Test
    void approveSeller_ShouldApproveSeller() {
        User seller = new User();
        seller.setId(1);
        seller.setApproved(false);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(seller));

        adminService.approveSeller(1);

        assertTrue(seller.getApproved());
        verify(userRepository).save(seller);
    }

    @Test
    void approveSeller_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminService.approveSeller(1));
    }

    @Test
    void declineSeller_ShouldDeleteSeller() {
        User seller = new User();
        seller.setId(1);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(seller));

        adminService.declineSeller(1);

        verify(userRepository).delete(seller);
    }

    @Test
    void declineSeller_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminService.declineSeller(1));
    }

    @Test
    void getPendingComments_ShouldReturnListOfPendingCommentDTOs() {
        User user = new User();
        user.setId(1);

        List<Comment> pendingComments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setId(1);
        comment.setUser(user);
        pendingComments.add(comment);

        when(commentRepository.findByApprovedFalse()).thenReturn(pendingComments);

        List<CommentDTO> result = adminService.getPendingComments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void reviewComment_ShouldApproveCommentAndCreateRating() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setApproved(false);

        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));

        adminService.reviewComment(1, true, 5);

        assertTrue(comment.getApproved());
        verify(commentRepository).save(comment);
        verify(ratingService).createRating(any());
    }

    @Test
    void reviewComment_ShouldThrowCommentNotFoundException() {
        when(commentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> adminService.reviewComment(1, true, 5));
    }

    @Test
    void reviewComment_ShouldThrowIllegalArgumentException_WhenRatingValueIsNull() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setApproved(false);

        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));

        assertThrows(IllegalArgumentException.class, () -> adminService.reviewComment(1, true, null));
    }
}