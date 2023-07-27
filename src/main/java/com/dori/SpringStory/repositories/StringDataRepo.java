package com.dori.SpringStory.repositories;

import com.dori.SpringStory.wzHandlers.wzEntities.StringData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StringDataRepo extends JpaRepository<StringData, Long>,
        JpaSpecificationExecutor<StringData> {

    // In Query, you need to use the entityName in the query -
    @Query("SELECT s FROM StringData s WHERE s.name =?1")
    Optional<StringData> findStringDataByName(String name);
}
