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
    public ResponseEntity<Void> createComment(@PathVariable Integer sellerId, @RequestBody CommentDTO commentDTO) {
        commentService.createComment(sellerId, commentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/sellers")
    public ResponseEntity<Void> createCommentWithSellerRequest(@RequestBody CommentWithSellerRequestDTO requestDTO) throws UserAlreadyExistsException {
        commentService.createCommentWithSellerRequest(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/sellers/{sellerId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsBySeller(@PathVariable Integer sellerId) {
        List<CommentDTO> comments = commentService.getCommentsBySellerId(sellerId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Integer commentId) {
        CommentDTO comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Integer commentId, @RequestBody CommentDTO commentDTO) {
        commentService.updateComment(commentId, commentDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{commentId}/approve")
    public ResponseEntity<Void> approveComment(@PathVariable Integer commentId, @RequestParam Boolean approved) {
        commentService.approveComment(commentId, approved);
        return ResponseEntity.ok().build();
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