package com.dori.SpringStory.services;

import com.dori.SpringStory.inventory.Equip;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.repositories.EquipRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Component
public class EquipService implements BaseService<Equip>{

    private static EquipRepo equipRepo;

    private static EquipService instance;

    public static EquipService getInstance(){
        if(instance == null){
            instance = new EquipService(equipRepo);
        }
        return instance;
    }

    @Autowired
    public EquipService(EquipRepo equipRepo){
        EquipService.equipRepo = equipRepo;
    }

    // Logger -
    private static final Logger logger = new Logger(EquipService.class);

    @Override
    public Optional<Equip> getEntityById(Long id) {
        return equipRepo.findById(id);
    }

    @Override
    public Optional<Equip> getEntityByName(String name) {
        //TODO
        return Optional.empty();
    }

    @Override
    public Optional<List<Equip>> getAllEntities() {
        return Optional.of(equipRepo.findAll());
    }

    @Override
    public Optional<List<Equip>> getEntitiesByIds(List<Long> ids) {
        return Optional.of(equipRepo.findAllById(ids));
    }

    @Override
    public void addNewEntity(Equip entity) {
        //TODO: i don't think there is a unique modifier to validate for equip?
        equipRepo.save(entity);
    }

    @Override
    public void saveAll(List<Equip> listToSave) {
        //TODO
    }

    @Override
    public void delete(Long entityID) {
        if(!equipRepo.existsById(entityID)){
            throw new IllegalStateException("There isn't a equip with that id - " + entityID);
        }
        else {
            equipRepo.deleteById(entityID);
        }
    }

    @Override
    public Equip update(Long entityID, Equip entityDataToUpdate) {
        //TODO:
        return null;
    }

    @Override
    public List<Equip> updateMultipleEntities(List<Equip> entitiesDataToUpdate) {
        //TODO:
        return null;
    }
}
