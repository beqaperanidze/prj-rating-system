package com.prjratingsystem.repository;

import com.prjratingsystem.dto.CommentDTO;
import com.prjratingsystem.model.Comment;
import com.prjratingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findBySellerId(User seller);
}
