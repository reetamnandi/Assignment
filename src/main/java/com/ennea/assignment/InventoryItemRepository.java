package com.ennea.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, JpaSpecificationExecutor {
}