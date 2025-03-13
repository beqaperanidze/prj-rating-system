package com.prjratingsystem.controller;

import com.prjratingsystem.dto.GameObjectDTO;
import com.prjratingsystem.exception.GameObjectNotFoundException;
import com.prjratingsystem.exception.UnauthorizedAccessException;
import com.prjratingsystem.service.GameObjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-objects")
public class GameObjectController {

    private final GameObjectService gameObjectService;

    public GameObjectController(GameObjectService gameObjectService) {
        this.gameObjectService = gameObjectService;
    }

    @PostMapping
    public ResponseEntity<GameObjectDTO> createGameObject(@RequestBody GameObjectDTO gameObjectDTO) {
        GameObjectDTO createdGameObject = gameObjectService.createGameObject(gameObjectDTO);
        return new ResponseEntity<>(createdGameObject, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameObjectDTO> getGameObjectById(@PathVariable Integer id) {
        GameObjectDTO gameObject = gameObjectService.getGameObjectById(id);
        return ResponseEntity.ok(gameObject);
    }

    @GetMapping
    public ResponseEntity<List<GameObjectDTO>> getAllGameObjects() {
        List<GameObjectDTO> gameObjects = gameObjectService.getAllGameObjects();
        return ResponseEntity.ok(gameObjects);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameObjectDTO> updateGameObject(@PathVariable Integer id, @RequestBody GameObjectDTO gameObjectDTO) {
        GameObjectDTO updatedGameObject = gameObjectService.updateGameObject(id, gameObjectDTO);
        return ResponseEntity.ok(updatedGameObject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGameObject(@PathVariable Integer id) {
        gameObjectService.deleteGameObject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GameObjectDTO>> getGameObjectsByUserId(@PathVariable Integer userId) {
        List<GameObjectDTO> gameObjects = gameObjectService.getGameObjectsByUserId(userId);
        return ResponseEntity.ok(gameObjects);
    }

    @ExceptionHandler(GameObjectNotFoundException.class)
    public ResponseEntity<String> handleGameObjectNotFoundException(GameObjectNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

}