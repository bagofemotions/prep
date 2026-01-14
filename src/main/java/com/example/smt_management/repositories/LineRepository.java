
package com.example.smt_management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.smt_management.entities.Line;

/**
 * Repository for Line entity
 * Supports pagination, sorting, and dynamic filtering
 */
@Repository
public interface LineRepository extends JpaRepository<Line, Long>, JpaSpecificationExecutor<Line> {
    
    /**
     * Find line by name (case-sensitive)
     */
    Optional<Line> findByLineName(String lineName);
    
    /**
     * Check if line name exists
     */
    Boolean existsByLineName(String lineName);
    
    /**
     * Find line by name (case-insensitive)
     */
    Optional<Line> findByLineNameIgnoreCase(String lineName);
    
    /**
     * Find all lines belonging to a specific floor
     */
    List<Line> findByFloorMappingId(Long floorId);
    
    /**
     * Count lines in a floor
     */
    Long countByFloorMappingId(Long floorId);
    
    /**
     * Check if line has any machines
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Machine m WHERE m.lineMapping.id = :lineId")
    Boolean hasMachines(Long lineId);
    
    /**
     * Count machines in a line
     */
    @Query("SELECT COUNT(m) FROM Machine m WHERE m.lineMapping.id = :lineId")
    Long countMachinesByLineId(Long lineId);
}