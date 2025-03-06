package com.prjratingsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RatingDTO {

    private Integer id;
    private Integer commentId;
    private Integer ratingValue;
}