package com.example.smt_management.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.smt_management.entities.Machine;
import com.example.smt_management.enums.MachineType;
import com.example.smt_management.repositories.EntitySpecifications;
import com.example.smt_management.repositories.MachineRepository;

/**
 * Enhanced Machine Service with validation, search, and business logic
 */
@Service
@Transactional
public class MachineService {
    
    private final MachineRepository machineRepository;
    private final LineService lineService;
    
    public MachineService(MachineRepository machineRepository, LineService lineService) {
        this.machineRepository = machineRepository;
        this.lineService = lineService;
    }
    
    // ==================== CRUD Operations ====================
    
    /**
     * Get all machines with pagination
     */
    public Page<Machine> getAllMachines(Pageable pageable) {
        return machineRepository.findAll(pageable);
    }
    
    /**
     * Get all machines without pagination
     */
    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }
    
    /**
     * Get machine by serial number
     */
    public Machine getMachineBySerial(String machineSerial) {
        return machineRepository.findById(machineSerial)
                .orElseThrow(() -> new RuntimeException("Machine not found with serial: " + machineSerial));
    }
    
    /**
     * Save new machine or update existing one
     */
    public Machine saveMachine(Machine machine) {
        // For new machines, check if serial already exists
        if (!machineRepository.existsById(machine.getMachineSerial())) {
            validateMachineSerialUnique(machine.getMachineSerial());
            
            // Validate parent line can accept more machines
            if (machine.getLineMapping() != null) {
                lineService.validateCanAddMachine(machine.getLineMapping().getId());
            }
        } else {
            // For updates, check if line changed and validate new line
            Machine existing = getMachineBySerial(machine.getMachineSerial());
            if (machine.getLineMapping() != null && 
                !machine.getLineMapping().getId().equals(existing.getLineMapping().getId())) {
                lineService.validateCanAddMachine(machine.getLineMapping().getId());
            }
        }
        
        return machineRepository.save(machine);
    }
    
    /**
     * Delete machine
     */
    public void deleteMachine(String machineSerial) {
        if (!machineRepository.existsById(machineSerial)) {
            throw new RuntimeException("Machine not found with serial: " + machineSerial);
        }
        machineRepository.deleteById(machineSerial);
    }
    
    // ==================== Search & Filter ====================
    
    /**
     * Search machines with filters
     */
    public Page<Machine> searchMachines(
            String model,
            MachineType type,
            Integer yearOfManufacturing,
            String manufacturingCompany,
            Long lineId,
            Pageable pageable) {
        
        Specification<Machine> spec = EntitySpecifications.filterMachines(
            model, type, yearOfManufacturing, manufacturingCompany, lineId
        );
        return machineRepository.findAll(spec, pageable);
    }
    
    /**
     * Get all machines for a specific line
     */
    public List<Machine> getMachinesByLineId(Long lineId) {
        return machineRepository.findByLineMappingId(lineId);
    }
    
    /**
     * Find machines by type
     */
    public List<Machine> findByType(MachineType type) {
        return machineRepository.findByType(type);
    }
    
    /**
     * Find machines by manufacturing company (case-insensitive)
     */
    public List<Machine> findByManufacturingCompany(String company) {
        return machineRepository.findByManufacturingCompanyContainingIgnoreCase(company);
    }
    
    /**
     * Find machines by year of manufacturing
     */
    public List<Machine> findByYearOfManufacturing(Integer year) {
        return machineRepository.findByYearOfManufacturing(year);
    }
    
    /**
     * Find machines by model (case-insensitive)
     */
    public List<Machine> findByModel(String model) {
        return machineRepository.findByModelContainingIgnoreCase(model);
    }
    
    // ==================== Validation Methods ====================
    
    /**
     * Check if machine serial already exists
     */
    public boolean existsByMachineSerial(String machineSerial) {
        return machineRepository.existsByMachineSerial(machineSerial);
    }
    
    /**
     * Validate machine serial is unique
     * @throws RuntimeException if serial already exists
     */
    private void validateMachineSerialUnique(String machineSerial) {
        if (existsByMachineSerial(machineSerial)) {
            throw new RuntimeException("Machine serial '" + machineSerial + "' already exists");
        }
    }
    
    /**
     * Count machines in a specific line
     */
    public Long countMachinesByLineId(Long lineId) {
        return machineRepository.countByLineMappingId(lineId);
    }
    
    // ==================== Statistics ====================
    
    /**
     * Get total number of machines
     */
    public long getTotalCount() {
        return machineRepository.count();
    }
    
    /**
     * Get summary for dashboard
     */
    public String getSummary(String machineSerial) {
        Machine machine = getMachineBySerial(machineSerial);
        return machine.getModel() + " (Serial: " + machineSerial + 
               ", Line: " + machine.getLineName() + 
               ", Floor: " + machine.getFloorName() + ")";
    }
    
    /**
     * Get count by machine type
     */
    public long countByType(MachineType type) {
        return machineRepository.findByType(type).size();
    }
}