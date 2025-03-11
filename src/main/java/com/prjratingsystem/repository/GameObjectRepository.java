package com.prjratingsystem.repository;

import com.prjratingsystem.model.GameObject;
import com.prjratingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameObjectRepository extends JpaRepository<GameObject, Integer> {
    List<GameObject> findByUser(User user);

    void deleteAllByUserId(Integer id);
}
