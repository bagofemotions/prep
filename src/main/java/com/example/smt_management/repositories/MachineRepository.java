
package com.example.smt_management.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.smt_management.entities.Machine;
import com.example.smt_management.enums.MachineType;

/**
 * Repository for Machine entity
 * Supports pagination, sorting, and dynamic filtering
 */
@Repository
public interface MachineRepository extends JpaRepository<Machine, String>, JpaSpecificationExecutor<Machine> {
    
    /**
     * Find all machines belonging to a specific line
     */
    List<Machine> findByLineMappingId(Long lineId);
    
    /**
     * Count machines in a line
     */
    Long countByLineMappingId(Long lineId);
    
    /**
     * Find machines by type
     */
    List<Machine> findByType(MachineType type);
    
    /**
     * Find machines by manufacturing company
     */
    List<Machine> findByManufacturingCompanyContainingIgnoreCase(String company);
    
    /**
     * Find machines by year of manufacturing
     */
    List<Machine> findByYearOfManufacturing(Integer year);
    
    /**
     * Find machines by model (case-insensitive)
     */
    List<Machine> findByModelContainingIgnoreCase(String model);
    
    /**
     * Check if machine serial exists
     */
    Boolean existsByMachineSerial(String machineSerial);
}