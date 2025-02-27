package com.prjratingsystem.service;

import com.prjratingsystem.dto.GameObjectDTO;

import java.util.List;

public interface GameObjectService {

    /**
     * Creates a new GameObject.
     *
     * @param gameObjectDTO The DTO containing the GameObject data.
     * @return The created GameObjectDTO.
     */
    GameObjectDTO createGameObject(GameObjectDTO gameObjectDTO);

    /**
     * Retrieves a GameObject by its ID.
     *
     * @param id The ID of the GameObject.
     * @return The GameObjectDTO.
     */
    GameObjectDTO getGameObjectById(Integer id);

    /**
     * Retrieves all GameObjects.
     *
     * @return A list of GameObjectDTOs.
     */
    List<GameObjectDTO> getAllGameObjects();

    /**
     * Updates an existing GameObject.
     *
     * @param id The ID of the GameObject to update.
     * @param gameObjectDTO The DTO containing the updated GameObject data.
     * @return The updated GameObjectDTO.
     */
    GameObjectDTO updateGameObject(Integer id, GameObjectDTO gameObjectDTO);

    /**
     * Deletes a GameObject by its ID.
     *
     * @param id The ID of the GameObject to delete.
     */
    void deleteGameObject(Integer id);

    /**
     * Retrieves all GameObjects associated with a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of GameObjectDTOs associated with the user.
     */
    List<GameObjectDTO> getGameObjectsByUserId(Integer userId);
}