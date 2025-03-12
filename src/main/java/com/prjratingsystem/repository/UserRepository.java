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
}
