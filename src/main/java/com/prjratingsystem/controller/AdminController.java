package com.prjratingsystem.controller;

import com.prjratingsystem.dto.RatingDTO;
import com.prjratingsystem.dto.UserDTO;
import com.prjratingsystem.service.AdminService;
import com.prjratingsystem.service.CommentService;
import com.prjratingsystem.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RatingService ratingService;
    private final CommentService commentService;
    private final AdminService adminService;

    public AdminController(RatingService ratingService, CommentService commentService, AdminService adminService) {
        this.ratingService = ratingService;
        this.commentService = commentService;
        this.adminService = adminService;
    }

    @PostMapping("/ratings")
    public ResponseEntity<RatingDTO> createRating(@RequestBody RatingDTO ratingDTO) {
        RatingDTO createdRating = ratingService.createRating(ratingDTO);
        return new ResponseEntity<>(createdRating, HttpStatus.CREATED);
    }

    @PatchMapping("/comments/{commentId}/approve")
    public ResponseEntity<Void> approveComment(
            @PathVariable Integer commentId,
            @RequestParam Integer ratingValue
    ) {
        commentService.approveComment(commentId, true);
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setCommentId(commentId);
        ratingDTO.setRatingValue(ratingValue);
        ratingService.createRating(ratingDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/comments/{commentId}/decline")
    public ResponseEntity<Void> declineComment(@PathVariable Integer commentId) {
        commentService.approveComment(commentId, false);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sellers/pending")
    public ResponseEntity<List<UserDTO>> getPendingSellers() {
        return ResponseEntity.ok(adminService.getPendingSellers());
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
}