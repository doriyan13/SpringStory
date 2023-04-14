package com.dori.SpringStory.services;

import java.util.List;
import java.util.Optional;

/**
 * This Interface is the Base Service POJO, which each implementation of service will have to implement.
 * ref -
 *      https://www.digitalocean.com/community/tutorials/java-generics-example-method-class-interface .
 *
 * @param <T> - The Entity Class the Service will have functionalities for.
 * @author Doriyan Esterin.
 */
public interface BaseService<T> {
    /**
     * Getting optional entity by ID.
     * @param id - ID of the Entity.
     * @return - Return optional entity.
     */
    Optional<T> getEntityById(Long id);

    Optional<T> getEntityByName(String name);

    /**
     * @return - Return list all entities from the db.
     */
    Optional<List<T>> getAllEntities();

    /**
     * This function is getting a list of ids and return optional list of entities.
     * @param ids - List of IDs you desire.
     * @return - Optional list of entities.
     */
    Optional<List<T>> getEntitiesByIds(List<Long> ids);

    /**
     * This function add the new entity into the DB.
     * @param entity - The entity you desire to save into the DB.
     */
    void addNewEntity(T entity);

    /**
     * This function add the list of new entities into the DB.
     * @param listToSave - The list of entities to save into the DB.
     */
    void saveAll(List<T> listToSave);

    /**
     * This function delete the entity with that ID from the DB.
     * @param entityID - The ID of the entity you desire to delete.
     */
    void delete(Long entityID);

    /**
     * This function is getting an entity ID and data to update and will attempt to update the entity data into the DB.
     * @param entityID - The ID of the entity you desire to update.
     * @param entityDataToUpdate - The data you desire to update.
     * @return - Return the updated entity if succeeded.
     */
    T update(Long entityID,T entityDataToUpdate);

    /**
     * This function is getting a list of entities (must contain at least ID and data to change) and will attempt to update the entities data into the DB.
     * @param entitiesDataToUpdate - List of entities data to update (for each entry must have the ID).
     * @return - Return the list of entities that were updated.
     */
    List<T> updateMultipleEntities(List<T> entitiesDataToUpdate);

}