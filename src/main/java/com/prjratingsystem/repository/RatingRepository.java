package com.prjratingsystem.repository;

import com.prjratingsystem.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByComment_User_Id(Integer sellerId);

     void deleteAllByCommentUserId(Integer id);
}
