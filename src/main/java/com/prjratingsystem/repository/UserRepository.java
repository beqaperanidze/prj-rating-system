package com.prjratingsystem.repository;

import com.prjratingsystem.model.enums.Role;
import com.prjratingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByRole(Role roleEnum);

    boolean existsByEmail(String email);

    List<User> findByRoleAndApprovedFalse(Role role);

    Optional<User> findByEmail(String email);

}
