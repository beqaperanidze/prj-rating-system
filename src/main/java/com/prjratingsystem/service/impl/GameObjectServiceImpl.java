package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.GameObjectDTO;
import com.prjratingsystem.exception.GameObjectNotFoundException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.GameObject;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.GameObjectRepository;
import com.prjratingsystem.repository.UserRepository;
import com.prjratingsystem.service.GameObjectService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameObjectServiceImpl implements GameObjectService {

    private final GameObjectRepository gameObjectRepository;
    private final UserRepository userRepository;

    public GameObjectServiceImpl(GameObjectRepository gameObjectRepository, UserRepository userRepository) {
        this.gameObjectRepository = gameObjectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public GameObjectDTO createGameObject(GameObjectDTO gameObjectDTO) {
        GameObject gameObject = new GameObject();
        gameObject.setTitle(gameObjectDTO.getTitle());
        gameObject.setText(gameObjectDTO.getText());
        User user = userRepository.findById(gameObjectDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: %d".formatted(gameObjectDTO.getUserId())));
        gameObject.setUser(user);

        GameObject savedGameObject = gameObjectRepository.save(gameObject);
        return mapToGameObjectDTO(savedGameObject);
    }

    @Override
    public GameObjectDTO getGameObjectById(Integer id) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new GameObjectNotFoundException("GameObject not found with ID: %d".formatted(id)));
        return mapToGameObjectDTO(gameObject);
    }

    @Override
    public List<GameObjectDTO> getAllGameObjects() {
        return gameObjectRepository.findAll().stream().map(this::mapToGameObjectDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GameObjectDTO updateGameObject(Integer id, GameObjectDTO gameObjectDTO) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new GameObjectNotFoundException("GameObject not found with ID: %d".formatted(id)));

        gameObject.setTitle(gameObjectDTO.getTitle());
        gameObject.setText(gameObjectDTO.getText());
        gameObject.setUpdatedAt(LocalDateTime.now());

        GameObject savedGameObject = gameObjectRepository.save(gameObject);
        return mapToGameObjectDTO(savedGameObject);
    }

    @Override
    @Transactional
    public void deleteGameObject(Integer id) {
        GameObject gameObject = gameObjectRepository.findById(id)
                .orElseThrow(() -> new GameObjectNotFoundException("GameObject not found with ID: %d".formatted(id)));
        gameObjectRepository.delete(gameObject);
    }

    @Override
    public List<GameObjectDTO> getGameObjectsByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: %d".formatted(userId)));
        return gameObjectRepository.findByUser(user).stream().map(this::mapToGameObjectDTO).collect(Collectors.toList());
    }

    private GameObjectDTO mapToGameObjectDTO(GameObject gameObject) {
        GameObjectDTO gameObjectDTO = new GameObjectDTO();

        gameObjectDTO.setId(gameObject.getId());
        gameObjectDTO.setTitle(gameObject.getTitle());
        gameObjectDTO.setText(gameObject.getText());
        gameObjectDTO.setUserId(gameObject.getUser().getId());
        gameObjectDTO.setCreatedAt(gameObject.getCreatedAt());
        gameObjectDTO.setUpdatedAt(gameObject.getUpdatedAt());

        return gameObjectDTO;
    }
}