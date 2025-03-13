package com.prjratingsystem.repository;

import com.prjratingsystem.model.enums.Role;
import com.prjratingsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByRole(Role roleEnum);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findByApprovedFalseAndRole(Role role);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN GameObject g ON g.user.id = u.id " +
            "WHERE u.role = 'SELLER' " +
            "AND (:gameTitle IS NULL OR g.title = :gameTitle) " +
            "AND (:minRating IS NULL OR :maxRating IS NULL OR " +
            "(COALESCE((SELECT AVG(r.ratingValue) FROM Rating r WHERE r.comment.user.id = u.id), 0) BETWEEN :minRating AND :maxRating))")
    List<User> findSellersByGameAndRating(@Param("gameTitle") String gameTitle,
                                          @Param("minRating") Double minRating,
                                          @Param("maxRating") Double maxRating);


}
