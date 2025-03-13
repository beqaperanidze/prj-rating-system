package com.prjratingsystem.controller;

import com.prjratingsystem.dto.CommentDTO;
import com.prjratingsystem.dto.CommentWithSellerRequestDTO;
import com.prjratingsystem.exception.CommentNotFoundException;
import com.prjratingsystem.exception.UserAlreadyExistsException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/sellers/{sellerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO createComment(@PathVariable Integer sellerId, @RequestBody CommentDTO commentDTO) {
        return commentService.createComment(sellerId, commentDTO);
    }

    @PostMapping("/sellers")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO createCommentWithSellerRequest(@RequestBody CommentWithSellerRequestDTO requestDTO) throws UserAlreadyExistsException {
        return commentService.createCommentWithSellerRequest(requestDTO);
    }

    @GetMapping("/sellers/{sellerId}/comments")
    public List<CommentDTO> getCommentsBySeller(@PathVariable Integer sellerId) {
        return commentService.getCommentsBySellerId(sellerId);
    }

    @GetMapping("/{commentId}")
    public CommentDTO getCommentById(@PathVariable Integer commentId) {
        return commentService.getCommentById(commentId);
    }

    @PutMapping("/{commentId}")
    public CommentDTO updateComment(@PathVariable Integer commentId, @RequestBody CommentDTO commentDTO) {
        return commentService.updateComment(commentId, commentDTO);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
    }

    @PatchMapping("/{commentId}/approve")
    public CommentDTO approveComment(@PathVariable Integer commentId, @RequestParam Boolean approved) {
        return commentService.approveComment(commentId, approved);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleCommentNotFoundException(CommentNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}