package com.prjratingsystem.service;

import com.prjratingsystem.dto.CommentDTO;
import com.prjratingsystem.dto.UserDTO;

import java.util.List;

public interface AdminService {

    /**
     * Retrieves a list of sellers pending approval.
     *
     * @return List of UserDTO objects representing the pending sellers
     */
    List<UserDTO> getPendingSellers();

    /**
     * Approves a seller based on the provided seller ID.
     *
     * @param sellerId The unique identifier of the seller to approve
     */
    void approveSeller(Integer sellerId);

    /**
     * Declines a seller based on the provided seller ID.
     *
     * @param sellerId The unique identifier of the seller to decline
     */
    void declineSeller(Integer sellerId);

    /**
     * Reviews a comment based on the provided comment ID, approval status, and rating value.
     *
     * @param commentId   The unique identifier of the comment to review
     * @param approved    The approval status of the comment
     * @param ratingValue The rating value to assign to the comment
     */
    void reviewComment(Integer commentId, boolean approved, Integer ratingValue);

    /**
     * Retrieves a list of comments pending approval.
     *
     * @return List of CommentDTO objects representing the pending comments
     */
    List<CommentDTO> getPendingComments();
}