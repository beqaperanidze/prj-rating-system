package com.prjratingsystem.repository;

import com.prjratingsystem.model.GameObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameObjectRepository extends JpaRepository<GameObject, Integer> {
}
