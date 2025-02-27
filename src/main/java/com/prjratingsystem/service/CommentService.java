package com.prjratingsystem.service;

import com.prjratingsystem.dto.CommentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    /**
     * Creates a new comment for a seller.
     *
     * @param sellerId   The ID of the seller being commented on
     * @param commentDTO The comment details
     * @return The created comment
     */
    CommentDTO createComment(Integer sellerId, CommentDTO commentDTO);

    /**
     * Retrieves all comments for a specific seller.
     *
     * @param sellerId The ID of the seller
     * @return List of comments for the seller
     */
    List<CommentDTO> getCommentsBySellerId(Integer sellerId);

    /**
     * Retrieves a specific comment by its ID.
     *
     * @param commentId The ID of the comment
     * @return The comment if found
     */
    CommentDTO getCommentById(Integer commentId);

    /**
     * Updates an existing comment.
     *
     * @param commentId  The ID of the comment to update
     * @param commentDTO The updated comment details
     * @return The updated comment
     */
    CommentDTO updateComment(Integer commentId, CommentDTO commentDTO);

    /**
     * Deletes a comment (only allowed by the author).
     *
     * @param commentId The ID of the comment to delete
     */
    void deleteComment(Integer commentId);

    /**
     * Approves or rejects a comment (admin function).
     *
     * @param commentId The ID of the comment
     * @param approved  Whether to approve or reject the comment
     * @return The updated comment
     */
    CommentDTO approveComment(Integer commentId, boolean approved);
}
