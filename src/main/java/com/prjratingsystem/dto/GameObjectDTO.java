package com.prjratingsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class GameObjectDTO {
    private Integer id;
    private String title;
    private String text;
    private Integer userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}