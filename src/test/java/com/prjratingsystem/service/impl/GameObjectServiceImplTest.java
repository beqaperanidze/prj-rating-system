package com.prjratingsystem.service.impl;

import com.prjratingsystem.dto.GameObjectDTO;
import com.prjratingsystem.exception.GameObjectNotFoundException;
import com.prjratingsystem.exception.UserNotFoundException;
import com.prjratingsystem.model.GameObject;
import com.prjratingsystem.model.User;
import com.prjratingsystem.repository.GameObjectRepository;
import com.prjratingsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameObjectServiceImplTest {

    @Mock
    private GameObjectRepository gameObjectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GameObjectServiceImpl gameObjectService;

    private User testUser;
    private GameObject testGameObject;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");

        testGameObject = new GameObject();
        testGameObject.setId(1);
        testGameObject.setTitle("Test Game");
        testGameObject.setText("Test Description");
        testGameObject.setUser(testUser);
    }

    @Test
    void createGameObject_ShouldReturnGameObjectDTO() {
        GameObjectDTO gameObjectDTO = new GameObjectDTO();
        gameObjectDTO.setTitle("Title");
        gameObjectDTO.setText("Text");
        gameObjectDTO.setUserId(1);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));
        when(gameObjectRepository.save(any(GameObject.class))).thenReturn(testGameObject);

        GameObjectDTO result = gameObjectService.createGameObject(gameObjectDTO);

        assertNotNull(result);
        assertEquals("Test Game", result.getTitle());
        assertEquals("Test Description", result.getText());
        assertEquals(1, result.getUserId());
        verify(userRepository).findById(1);
        verify(gameObjectRepository).save(any(GameObject.class));
    }

    @Test
    void createGameObject_ShouldThrowUserNotFoundException() {
        GameObjectDTO gameObjectDTO = new GameObjectDTO();
        gameObjectDTO.setUserId(999);

        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> gameObjectService.createGameObject(gameObjectDTO));
        verify(userRepository).findById(999);
        verify(gameObjectRepository, never()).save(any(GameObject.class));
    }

    @Test
    void getGameObjectById_ShouldReturnGameObjectDTO() {
        when(gameObjectRepository.findById(1)).thenReturn(Optional.of(testGameObject));

        GameObjectDTO result = gameObjectService.getGameObjectById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Game", result.getTitle());
        assertEquals("Test Description", result.getText());
        assertEquals(1, result.getUserId());
        verify(gameObjectRepository).findById(1);
    }

    @Test
    void getGameObjectById_ShouldThrowGameObjectNotFoundException() {
        when(gameObjectRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(GameObjectNotFoundException.class, () -> gameObjectService.getGameObjectById(999));
        verify(gameObjectRepository).findById(999);
    }

    @Test
    void getAllGameObjects_ShouldReturnListOfGameObjectDTOs() {
        List<GameObject> gameObjects = new ArrayList<>();
        gameObjects.add(testGameObject);

        GameObject gameObject2 = new GameObject();
        gameObject2.setId(2);
        gameObject2.setTitle("Another Game");
        gameObject2.setText("Another Description");
        gameObject2.setUser(testUser);
        gameObjects.add(gameObject2);

        when(gameObjectRepository.findAll()).thenReturn(gameObjects);

        List<GameObjectDTO> result = gameObjectService.getAllGameObjects();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Game", result.get(0).getTitle());
        assertEquals("Another Game", result.get(1).getTitle());
        assertEquals(1, result.get(0).getUserId());
        assertEquals(1, result.get(1).getUserId());
        verify(gameObjectRepository).findAll();
    }

    @Test
    void updateGameObject_ShouldReturnUpdatedGameObjectDTO() {
        GameObjectDTO updateDTO = new GameObjectDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setText("Updated Text");

        GameObject originalGameObject = new GameObject();
        originalGameObject.setId(1);
        originalGameObject.setTitle("Original Title");
        originalGameObject.setText("Original Text");
        originalGameObject.setUser(testUser);

        GameObject updatedGameObject = new GameObject();
        updatedGameObject.setId(1);
        updatedGameObject.setTitle("Updated Title");
        updatedGameObject.setText("Updated Text");
        updatedGameObject.setUser(testUser);

        when(gameObjectRepository.findById(1)).thenReturn(Optional.of(originalGameObject));
        when(gameObjectRepository.save(any(GameObject.class))).thenReturn(updatedGameObject);

        GameObjectDTO result = gameObjectService.updateGameObject(1, updateDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Text", result.getText());
        assertEquals(1, result.getUserId());
        verify(gameObjectRepository).findById(1);
        verify(gameObjectRepository).save(any(GameObject.class));
    }

    @Test
    void updateGameObject_ShouldThrowGameObjectNotFoundException() {
        GameObjectDTO updateDTO = new GameObjectDTO();
        updateDTO.setTitle("Updated Title");

        when(gameObjectRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(GameObjectNotFoundException.class, () -> gameObjectService.updateGameObject(999, updateDTO));
        verify(gameObjectRepository).findById(999);
        verify(gameObjectRepository, never()).save(any(GameObject.class));
    }

    @Test
    void deleteGameObject_ShouldDeleteGameObject() {
        when(gameObjectRepository.findById(1)).thenReturn(Optional.of(testGameObject));
        doNothing().when(gameObjectRepository).delete(any(GameObject.class));

        gameObjectService.deleteGameObject(1);

        verify(gameObjectRepository).findById(1);
        verify(gameObjectRepository).delete(testGameObject);
    }

    @Test
    void deleteGameObject_ShouldThrowGameObjectNotFoundException() {
        when(gameObjectRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(GameObjectNotFoundException.class, () -> gameObjectService.deleteGameObject(999));
        verify(gameObjectRepository).findById(999);
        verify(gameObjectRepository, never()).delete(any(GameObject.class));
    }

    @Test
    void getGameObjectsByUserId_ShouldReturnListOfGameObjectDTOs() {
        List<GameObject> gameObjects = new ArrayList<>();
        gameObjects.add(testGameObject);

        GameObject gameObject2 = new GameObject();
        gameObject2.setId(2);
        gameObject2.setTitle("Another Game");
        gameObject2.setText("Another Description");
        gameObject2.setUser(testUser);
        gameObjects.add(gameObject2);

        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(gameObjectRepository.findByUser(testUser)).thenReturn(gameObjects);

        List<GameObjectDTO> result = gameObjectService.getGameObjectsByUserId(1);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Game", result.get(0).getTitle());
        assertEquals("Another Game", result.get(1).getTitle());
        assertEquals(1, result.get(0).getUserId());
        assertEquals(1, result.get(1).getUserId());
        verify(userRepository).findById(1);
        verify(gameObjectRepository).findByUser(testUser);
    }

    @Test
    void getGameObjectsByUserId_ShouldThrowUserNotFoundException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> gameObjectService.getGameObjectsByUserId(999));
        verify(userRepository).findById(999);
        verify(gameObjectRepository, never()).findByUser(any(User.class));
    }
}