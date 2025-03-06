package com.prjratingsystem.dto;

import lombok.Data;

@Data
public class CommentWithSellerRequestDTO {
    private String message;
    private String sellerFirstName;
    private String sellerLastName;
    private String sellerEmail;
    private String sellerDescription;
    private Integer gameId;
}