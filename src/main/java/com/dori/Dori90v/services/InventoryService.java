package com.dori.Dori90v.services;

import com.dori.Dori90v.inventory.Inventory;
import com.dori.Dori90v.logger.Logger;
import com.dori.Dori90v.repositories.InventoryRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class InventoryService implements BaseService<Inventory> {

    private static InventoryRepo equipRepo;

    private static InventoryService instance;

    public static InventoryService getInstance(){
        if(instance == null){
            instance = new InventoryService(equipRepo);
        }
        return instance;
    }

    @Autowired
    public InventoryService(InventoryRepo equipRepo){
        InventoryService.equipRepo = equipRepo;
    }

    // Logger -
    private static final Logger logger = new Logger(InventoryService.class);

    @Override
    public Optional<Inventory> getEntityById(Long id) {
        return equipRepo.findById(id);
    }

    @Override
    public Optional<Inventory> getEntityByName(String name) {
        //TODO
        return Optional.empty();
    }

    @Override
    public Optional<List<Inventory>> getAllEntities() {
        return Optional.of(equipRepo.findAll());
    }

    @Override
    public Optional<List<Inventory>> getEntitiesByIds(List<Long> ids) {
        return Optional.of(equipRepo.findAllById(ids));
    }

    @Override
    public void addNewEntity(Inventory entity) {
        //TODO: i don't think there is a unique modifier to validate for equip?
        equipRepo.save(entity);
    }

    @Override
    public void saveAll(List<Inventory> listToSave) {
        //TODO:
    }

    @Override
    public void delete(Long entityID) {
        if(!equipRepo.existsById(entityID)){
            throw new IllegalStateException("There isn't a inventory with that id - " + entityID);
        }
        else {
            equipRepo.deleteById(entityID);
        }
    }

    @Override
    public Inventory update(Long entityID, Inventory entityDataToUpdate) {
        //TODO:
        return null;
    }

    @Override
    public List<Inventory> updateMultipleEntities(List<Inventory> entitiesDataToUpdate) {
        //TODO:
        return null;
    }
}
