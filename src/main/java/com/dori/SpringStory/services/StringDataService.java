package com.dori.SpringStory.services;

import com.dori.SpringStory.enums.StringDataType;
import com.dori.SpringStory.logger.Logger;
import com.dori.SpringStory.repositories.StringDataRepo;
import com.dori.SpringStory.dataHandlers.dataEntities.StringData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@Component
public class StringDataService implements BaseService<StringData> {

    private static StringDataRepo stringRepo;

    private static StringDataService instance;

    // Logger -
    private static final Logger logger = new Logger(MapleAccountService.class);

    @Autowired
    public StringDataService(StringDataRepo stringRepo) {
        StringDataService.stringRepo = stringRepo;
    }

    public static StringDataService getInstance() {
        if (instance == null) {
            instance = new StringDataService(stringRepo);
        }
        return instance;
    }

    static Specification<StringData> isType(StringDataType type) {
        return (entity, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(entity.get("type"), type);
    }

    static Specification<StringData> containName(String regexName) {
        return (entity, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(entity.get("name"), "%" + regexName + "%");
    }

    static Specification<StringData> startWithName(String regexName) {
        return (entity, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(entity.get("name"), regexName + "%");
    }

    static Specification<StringData> endWithName(String regexName) {
        return (entity, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(entity.get("name"), "%" + regexName);
    }

    public Optional<List<StringData>> findStringByNameAndType(String regexName, StringDataType type) {
        // Using Criteria queries to get all the desired results -
        List<StringData> retVal = stringRepo.findAll(
                where(isType(type))
                        // Get all the StringData that contain the regex word -
                        .and(containName(regexName)
                                // Get all the StringData that start with the regex word -
                                .or(startWithName(regexName)
                                        // Get all the StringData that end with the regex word -
                                        .or(endWithName(regexName))
                                )
                        )
        );
        // Return an optional list of all the results -
        return Optional.of(retVal);
    }

    @Override
    public Optional<StringData> getEntityById(Long id) {
        return stringRepo.findById(id);
    }

    @Override
    public Optional<StringData> getEntityByName(String name) {
        return stringRepo.findStringDataByName(name);
    }

    @Override
    public Optional<List<StringData>> getAllEntities() {
        return Optional.of(stringRepo.findAll());
    }

    @Override
    public Optional<List<StringData>> getEntitiesByIds(List<Long> ids) {
        return Optional.of(stringRepo.findAllById(ids));
    }

    @Override
    public void addNewEntity(StringData entity) {
        Optional<StringData> stringDataFromDB = stringRepo.findById(entity.getId());
        if (stringDataFromDB.isPresent()) {
            logger.error("The StringData is already exist! |" + entity.getId());
        } else {
            stringRepo.save(entity);
        }
    }

    @Override
    public void saveAll(List<StringData> listToSave) {
        // Get All the IDs to check -
        Set<Long> idsToCheck = new HashSet<>();
        listToSave.forEach(stringData -> idsToCheck.add(stringData.getId()));
        // Attempt get all the already existing entities from the DB -
        List<StringData> existingEntities = stringRepo.findAllById(idsToCheck);
        // If there are entities to remove, remove -
        if (!existingEntities.isEmpty()) {
            Set<Long> existingIds = new HashSet<>();
            existingEntities.forEach(stringData -> existingIds.add(stringData.getId()));
            listToSave = listToSave.stream().filter(stringData -> existingIds.contains(stringData.getId())).toList();
        }
        if (!listToSave.isEmpty()) {
            stringRepo.saveAll(listToSave);
        }
    }

    @Override
    public void delete(Long entityID) {
        if (!stringRepo.existsById(entityID)) {
            logger.error("There isn't a StringData with that id - " + entityID);
        } else {
            stringRepo.deleteById(entityID);
        }
    }

    @Override
    public StringData update(Long entityID, StringData entityDataToUpdate) {
        return stringRepo.save(entityDataToUpdate);
    }

    @Override
    public List<StringData> updateMultipleEntities(List<StringData> entitiesDataToUpdate) {
        return null;
    }
}
