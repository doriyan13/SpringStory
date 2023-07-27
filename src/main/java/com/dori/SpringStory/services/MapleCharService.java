package com.dori.SpringStory.services;

import com.dori.SpringStory.client.character.MapleChar;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.repositories.MapleCharRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Component
public class MapleCharService implements BaseService<MapleChar>{

    // The Maple account repository -
    private static MapleCharRepo charRepo;

    private static MapleCharService instance;

    // Logger -
    private static final Logger logger = new Logger(MapleAccountService.class);

    @Autowired
    public MapleCharService(MapleCharRepo charRepo){
        MapleCharService.charRepo = charRepo;
    }


    public static MapleCharService getInstance(){
        if(instance == null){
            instance = new MapleCharService(charRepo);
        }
        return instance;
    }

    @Override
    public Optional<MapleChar> getEntityById(Long id) {
        return charRepo.findById(id);
    }

    @Override
    public Optional<MapleChar> getEntityByName(String charName) {
        return charRepo.findMapleCharByName(charName);
    }

    @Override
    public Optional<List<MapleChar>> getAllEntities() {
        return Optional.of(charRepo.findAll());
    }

    @Override
    public Optional<List<MapleChar>> getEntitiesByIds(List<Long> ids) {
        return Optional.of(charRepo.findAllById(ids));
    }

    @Override
    public void addNewEntity(MapleChar entity) {
        Optional<MapleChar> charFromDB = charRepo.findMapleCharByName(entity.getName());
        if(charFromDB.isPresent()){
            throw new IllegalStateException("The account is already exist!");
        }
        else {
            charRepo.save(entity);
        }
    }

    @Override
    public void saveAll(List<MapleChar> listToSave) {
        //TODO:
    }

    @Override
    public void delete(Long entityID) {
        if(!charRepo.existsById(entityID)){
            throw new IllegalStateException("There isn't a account with that id - " + entityID);
        }
        else {
            charRepo.deleteById(entityID);
        }
    }

    @Override
    public MapleChar update(Long entityID, MapleChar entityDataToUpdate) {
        return charRepo.save(entityDataToUpdate);
    }

    @Override
    public List<MapleChar> updateMultipleEntities(List<MapleChar> entitiesDataToUpdate) {
        return null;
    }
}
