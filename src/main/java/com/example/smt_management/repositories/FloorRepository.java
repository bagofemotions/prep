
package com.example.smt_management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.smt_management.entities.Floor;

/**
 * Repository for Floor entity
 * Supports pagination, sorting, and dynamic filtering
 */
@Repository
public interface FloorRepository extends JpaRepository<Floor, Long>, JpaSpecificationExecutor<Floor> {
    
    /**
     * Find floor by name (case-sensitive)
     */
    Optional<Floor> findByName(String name);
    
    /**
     * Check if floor name exists
     */
    Boolean existsByName(String name);
    
    /**
     * Find floor by name (case-insensitive)
     */
    Optional<Floor> findByNameIgnoreCase(String name);
    
    /**
     * Check if floor has any lines
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Line l WHERE l.floorMapping.id = :floorId")
    Boolean hasLines(Long floorId);
    
    /**
     * Count lines in a floor
     */
    @Query("SELECT COUNT(l) FROM Line l WHERE l.floorMapping.id = :floorId")
    Long countLinesByFloorId(Long floorId);
}
