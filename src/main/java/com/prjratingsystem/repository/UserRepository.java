package com.prjratingsystem.repository;

import com.prjratingsystem.model.Role;
import com.prjratingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByRole(Role roleEnum);

    boolean existsByEmail(String email);

    List<User> findByRoleAndApprovedFalse(Role role);
}
