package com.ennea.assignment;

import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class InventorySpecifications {

    /**
     * @return The Specification condition to check if an inventory item is in stock
     */
    public static Specification<InventoryItem> isInStock() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("stock"), 0); // Having positive stock
    }

    /**
     * @return The Specification condition to check if an inventory item has not expired
     */
    public static Specification<InventoryItem> isNotExpired() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("exp"), new Date()); // Having expiry date >= today
    }

    /**
     * @param supplier - The supplier name / ID to check
     * @return The Specification condition to check if an inventory item whose supplier name is like the input
     */
    public static Specification<InventoryItem> checkForSupplier(String supplier) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("supplier")), likeWrapper(supplier.toLowerCase())); // Checking for %supplier%
    }

    /**
     * @param name - The product name to check
     * @return The Specification condition to check if an inventory item whose product name is like the input
     */
    public static Specification<InventoryItem> checkForProductName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likeWrapper(name.toLowerCase())); // Checking for %product%
    }

    /**
     * @param input The string to be wrapped
     * @return The string wrapped with "%" on both ends
     */
    private static String likeWrapper(String input) {
        return "%" + input + "%";
    }

}
