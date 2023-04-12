package com.dori.Dori90v.repositories;

import com.dori.Dori90v.inventory.Equip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This is the Equips Repository.
 * @author Dori.
 */
@Repository
public interface EquipRepo extends JpaRepository<Equip, Long>,
        JpaSpecificationExecutor<Equip> {
    // Spring implement all the functions behind the scene so the interface is relatively empty ~

    // In Query, you need to use the entityName in the query -
    @Query("SELECT s FROM Equip s WHERE s.itemId =?1")
    Optional<Equip> findEquipByItemId(int itemID);
}
