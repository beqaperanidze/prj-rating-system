package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.RatingDTO;
import com.prjratingsystem.exception.CommentNotFoundException;
import com.prjratingsystem.model.Comment;
import com.prjratingsystem.model.Rating;
import com.prjratingsystem.repository.CommentRepository;
import com.prjratingsystem.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRating_ShouldReturnRatingDTO() {
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setCommentId(1);
        ratingDTO.setRatingValue(5);

        Comment comment = new Comment();
        comment.setId(1);

        Rating rating = new Rating();
        rating.setComment(comment);
        rating.setRatingValue(5);

        when(commentRepository.findById(anyInt())).thenReturn(Optional.of(comment));
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        RatingDTO result = ratingService.createRating(ratingDTO);

        assertNotNull(result);
        assertEquals(1, result.getCommentId());
        assertEquals(5, result.getRatingValue());
    }

    @Test
    void createRating_ShouldThrowCommentNotFoundException() {
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setCommentId(1);

        when(commentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> ratingService.createRating(ratingDTO));
    }

    @Test
    void calculateSellerRating_ShouldReturnAverageRating() {
        Rating rating1 = new Rating();
        rating1.setRatingValue(4);
        Rating rating2 = new Rating();
        rating2.setRatingValue(5);

        when(ratingRepository.findByComment_User_Id(anyInt())).thenReturn(List.of(rating1, rating2));

        Double averageRating = ratingService.calculateSellerRating(1);

        assertEquals(4.5, averageRating);
    }

    @Test
    void calculateSellerRating_ShouldReturnZero_WhenNoRatings() {
        when(ratingRepository.findByComment_User_Id(anyInt())).thenReturn(Collections.emptyList());

        Double averageRating = ratingService.calculateSellerRating(1);

        assertEquals(0.0, averageRating);
    }
}