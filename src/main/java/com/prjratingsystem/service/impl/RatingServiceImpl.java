package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.RatingDTO;
import com.prjratingsystem.exception.CommentNotFoundException;
import com.prjratingsystem.model.Comment;
import com.prjratingsystem.model.Rating;
import com.prjratingsystem.repository.CommentRepository;
import com.prjratingsystem.repository.RatingRepository;
import com.prjratingsystem.service.RatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;

    public RatingServiceImpl(RatingRepository ratingRepository, CommentRepository commentRepository) {
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional
    public RatingDTO createRating(RatingDTO ratingDTO) {
        Comment comment = commentRepository.findById(ratingDTO.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: %d".formatted(ratingDTO.getCommentId())));

        Rating rating = new Rating();
        rating.setComment(comment);
        rating.setRatingValue(ratingDTO.getRatingValue());

        Rating savedRating = ratingRepository.save(rating);
        return mapToRatingDTO(savedRating);
    }

    @Override
    public Double calculateSellerRating(Integer sellerId) {
        List<Rating> ratings = ratingRepository.findByComment_SellerId_Id(sellerId);

        if (ratings.isEmpty()) {
            return 0.0;
        }

        return ratings.stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0.0);
    }

    private RatingDTO mapToRatingDTO(Rating rating) {
        RatingDTO dto = new RatingDTO();
        dto.setId(rating.getId());
        dto.setCommentId(rating.getComment().getId());
        dto.setRatingValue(rating.getRatingValue());
        return dto;
    }
}