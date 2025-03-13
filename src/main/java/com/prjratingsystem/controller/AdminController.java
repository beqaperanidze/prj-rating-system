package com.prjratingsystem.controller;

import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.dto.CommentDTO;
import com.prjratingsystem.exception.CommentNotFoundException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.service.AdminService;
import com.prjratingsystem.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RatingService ratingService;
    private final AdminService adminService;

    public AdminController(RatingService ratingService, AdminService adminService) {
        this.ratingService = ratingService;
        this.adminService = adminService;
    }

    @GetMapping("/sellers/{sellerId}/average-rating")
    public ResponseEntity<Double> getSellerAverageRating(@PathVariable Integer sellerId) {
        Double averageRating = ratingService.calculateSellerRating(sellerId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/comments/pending")
    public ResponseEntity<List<CommentDTO>> getPendingComments() {
        List<CommentDTO> pendingComments = adminService.getPendingComments();
        return ResponseEntity.ok(pendingComments);
    }

    @GetMapping("/sellers/pending")
    public ResponseEntity<List<UserDTO>> getPendingSellers() {
        return ResponseEntity.ok(adminService.getPendingSellers());
    }

    @PatchMapping("/comments/{commentId}/review")
    public ResponseEntity<Void> reviewComment(
            @PathVariable Integer commentId,
            @RequestParam boolean approved,
            @RequestParam(required = false) Integer ratingValue) {
        adminService.reviewComment(commentId, approved, ratingValue);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/sellers/{sellerId}/approve")
    public ResponseEntity<Void> approveSeller(@PathVariable Integer sellerId) {
        adminService.approveSeller(sellerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/sellers/{sellerId}/decline")
    public ResponseEntity<Void> declineSeller(@PathVariable Integer sellerId) {
        adminService.declineSeller(sellerId);
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