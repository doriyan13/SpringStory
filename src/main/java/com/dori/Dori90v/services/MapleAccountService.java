package com.dori.Dori90v.services;

import com.dori.Dori90v.client.character.MapleAccount;
import com.dori.Dori90v.logger.Logger;
import com.dori.Dori90v.repositories.MapleAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Component
public class MapleAccountService implements BaseService<MapleAccount> {

    // The Maple account repository -
    private static MapleAccountRepo accountRepo;

    private static MapleAccountService instance;

    public static MapleAccountService getInstance(){
        if(instance == null){
            instance = new MapleAccountService(accountRepo);
        }
        return instance;
    }

    @Autowired
    public MapleAccountService(MapleAccountRepo accountRepo){
        MapleAccountService.accountRepo = accountRepo;
    }

    // Logger -
    private static final Logger logger = new Logger(MapleAccountService.class);

    @Override
    public Optional<MapleAccount> getEntityById(Long id) {
        return accountRepo.findById(id);
    }

    @Override
    public Optional<MapleAccount> getEntityByName(String accountName) {
        return accountRepo.findMapleAccountByName(accountName);
    }

    @Override
    public Optional<List<MapleAccount>> getAllEntities() {
        return Optional.of(accountRepo.findAll());
    }

    @Override
    public Optional<List<MapleAccount>> getEntitiesByIds(List<Long> ids) {
        return Optional.of(accountRepo.findAllById(ids));
    }

    @Override
    public void addNewEntity(MapleAccount entity) {
        Optional<MapleAccount> accountFromDB = accountRepo.findMapleAccountByName(entity.getName());
        if(accountFromDB.isPresent()){
            throw new IllegalStateException("The account is already exist!");
        }
        else {
            accountRepo.save(entity);
        }
    }

    @Override
    public void saveAll(List<MapleAccount> listToSave) {
        //TODO: need to handle it!
    }

    @Override
    public void delete(Long entityID) {
        if(!accountRepo.existsById(entityID)){
            throw new IllegalStateException("There isn't a account with that id - " + entityID);
        }
        else {
            accountRepo.deleteById(entityID);
        }

    }

    @Override
    public MapleAccount update(Long entityID, MapleAccount entityDataToUpdate) {
        // TODO: need to handle it!
        return null;
    }

    @Override
    public List<MapleAccount> updateMultipleEntities(List<MapleAccount> entitiesDataToUpdate) {
        // TODO: need to handle it!
        return null;
    }
}
