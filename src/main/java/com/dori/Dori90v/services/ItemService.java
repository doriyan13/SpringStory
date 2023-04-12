package com.dori.Dori90v.services;

import com.dori.Dori90v.inventory.Item;
import com.dori.Dori90v.logger.Logger;
import com.dori.Dori90v.repositories.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Component
public class ItemService implements BaseService<Item> {

    private static ItemRepo itemRepo;

    private static ItemService instance;

    public static ItemService getInstance(){
        if(instance == null){
            instance = new ItemService(itemRepo);
        }
        return instance;
    }

    @Autowired
    public ItemService(ItemRepo itemRepo){
        ItemService.itemRepo = itemRepo;
    }

    // Logger -
    private static final Logger logger = new Logger(ItemService.class);

    @Override
    public Optional<Item> getEntityById(Long id) {
        return itemRepo.findById(id);
    }

    @Override
    public Optional<Item> getEntityByName(String name) {
        //TODO
        return Optional.empty();
    }

    @Override
    public Optional<List<Item>> getAllEntities() {
        return Optional.of(itemRepo.findAll());
    }

    @Override
    public Optional<List<Item>> getEntitiesByIds(List<Long> ids) {
        return Optional.of(itemRepo.findAllById(ids));
    }

    @Override
    public void addNewEntity(Item entity) {
        //TODO: i don't think there is a unique modifier to validate for equip?
        itemRepo.save(entity);
    }

    @Override
    public void saveAll(List<Item> listToSave) {
        //TODO
    }

    @Override
    public void delete(Long entityID) {
        if(!itemRepo.existsById(entityID)){
            throw new IllegalStateException("There isn't an item with that id - " + entityID);
        }
        else {
            itemRepo.deleteById(entityID);
        }
    }

    @Override
    public Item update(Long entityID, Item entityDataToUpdate) {
        //TODO:
        return null;
    }

    @Override
    public List<Item> updateMultipleEntities(List<Item> entitiesDataToUpdate) {
        //TODO:
        return null;
    }
}
