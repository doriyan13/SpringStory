package com.dori.SpringStory.repositories;

import com.dori.SpringStory.inventory.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * This is the Items Repository.
 * @author Dori.
 */
@Repository
public interface ItemRepo extends JpaRepository<Item, Long>,
        JpaSpecificationExecutor<Item> {
    // Spring implement all the functions behind the scene so the interface is relatively empty ~
}
