package com.prjratingsystem.service;

import com.prjratingsystem.dto.RatingDTO;

public interface RatingService {

    RatingDTO createRating(RatingDTO ratingDTO);

    Double calculateSellerRating(Integer sellerId);
}