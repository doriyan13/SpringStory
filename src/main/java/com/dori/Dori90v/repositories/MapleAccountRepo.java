package com.dori.Dori90v.repositories;

import com.dori.Dori90v.client.character.MapleAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This is the MapleAccount Repository.
 * @author Dori.
 */
@Repository
public interface MapleAccountRepo
        extends JpaRepository<MapleAccount, Long>,
        JpaSpecificationExecutor<MapleAccount> {
    // Spring implement all the functions behind the scene so the interface is relatively empty ~

    // JpaSpecificationExecutor -> allows me to add 'Specification' which let you build complex queries in spring! (aka Criteria queries)
    // ref -> https://www.baeldung.com/spring-data-criteria-queries

    // In Query, you need to use the entityName in the query -
    @Query("SELECT s FROM account s WHERE s.name =?1")
    Optional<MapleAccount> findMapleAccountByName(String name);
}
