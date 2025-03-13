package com.prjratingsystem.service;

import com.prjratingsystem.dto.CommentDTO;
import com.prjratingsystem.dto.UserDTO;

import java.util.List;

public interface AdminService {
    List<UserDTO> getPendingSellers();

    void approveSeller(Integer sellerId);

    void declineSeller(Integer sellerId);

    void reviewComment(Integer commentId, boolean approved, Integer ratingValue);

    List<CommentDTO> getPendingComments();
}