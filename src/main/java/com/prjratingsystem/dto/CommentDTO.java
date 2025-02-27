package com.prjratingsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Integer id;
    private String message;
    private Integer authorId;
    private Integer sellerId;
    private LocalDateTime createdAt;
    private boolean approved;
}