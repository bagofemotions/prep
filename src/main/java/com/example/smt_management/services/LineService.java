package com.example.smt_management.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.smt_management.entities.Line;
import com.example.smt_management.entities.Machine;
import com.example.smt_management.enums.Direction;
import com.example.smt_management.enums.LaneType;
import com.example.smt_management.repositories.EntitySpecifications;
import com.example.smt_management.repositories.LineRepository;

/**
 * Enhanced Line Service with validation, search, and business logic
 */
@Service
@Transactional
public class LineService {
    
    private final LineRepository lineRepository;
    private final FloorService floorService;
    
    public LineService(LineRepository lineRepository, FloorService floorService) {
        this.lineRepository = lineRepository;
        this.floorService = floorService;
    }
    
    // ==================== CRUD Operations ====================
    
    /**
     * Get all lines with pagination
     */
    public Page<Line> getAllLines(Pageable pageable) {
        return lineRepository.findAll(pageable);
    }
    
    /**
     * Get all lines without pagination (for dropdowns)
     */
    public List<Line> getAllLinesForDropdown() {
        return lineRepository.findAll();
    }
    
    /**
     * Get line by ID
     */
    public Line getLineById(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Line not found with ID: " + id));
    }
    
    /**
     * Save new line or update existing one
     */
    public Line saveLine(Line line) {
        // Validate line name uniqueness (for new lines)
        if (line.getId() == null) {
            validateLineNameUnique(line.getLineName());
            
            // Validate parent floor can accept more lines
            if (line.getFloorMapping() != null) {
                floorService.validateCanAddLine(line.getFloorMapping().getId());
            }
        } else {
            // For updates, check if name changed and if new name is unique
            Line existing = getLineById(line.getId());
            if (!existing.getLineName().equals(line.getLineName())) {
                validateLineNameUnique(line.getLineName());
            }
            
            // If floor changed, validate new floor can accept the line
            if (line.getFloorMapping() != null && 
                !line.getFloorMapping().getId().equals(existing.getFloorMapping().getId())) {
                floorService.validateCanAddLine(line.getFloorMapping().getId());
            }
        }
        
        return lineRepository.save(line);
    }
    
    /**
     * Delete line with validation
     */
    public void deleteLine(Long id) {
        Line line = getLineById(id);
        
        // Check if line has machines
        if (hasMachines(id)) {
            Long machineCount = countMachines(id);
            throw new RuntimeException(
                "Cannot delete line '" + line.getLineName() + "'. " +
                "It has " + machineCount + " machine(s). Please delete all machines first or use cascade delete."
            );
        }
        
        lineRepository.deleteById(id);
    }
    
    /**
     * Force delete line with all its machines (cascade)
     */
    public void cascadeDeleteLine(Long id) {
        Line line = getLineById(id);
        lineRepository.delete(line); // Cascade will delete all machines
    }
    
    // ==================== Search & Filter ====================
    
    /**
     * Search lines with filters
     */
    public Page<Line> searchLines(
            String lineName, 
            LaneType lane, 
            Direction direction, 
            Long floorId, 
            Pageable pageable) {
        
        Specification<Line> spec = EntitySpecifications.filterLines(lineName, lane, direction, floorId);
        return lineRepository.findAll(spec, pageable);
    }
    
    /**
     * Get all lines for a specific floor
     */
    public List<Line> getLinesByFloorId(Long floorId) {
        return lineRepository.findByFloorMappingId(floorId);
    }
    
    /**
     * Find line by exact name
     */
    public Optional<Line> findByLineName(String lineName) {
        return lineRepository.findByLineName(lineName);
    }
    
    /**
     * Find line by name (case-insensitive)
     */
    public Optional<Line> findByLineNameIgnoreCase(String lineName) {
        return lineRepository.findByLineNameIgnoreCase(lineName);
    }
    
    // ==================== Validation Methods ====================
    
    /**
     * Check if line name already exists
     */
    public boolean existsByLineName(String lineName) {
        return lineRepository.existsByLineName(lineName);
    }
    
    /**
     * Validate line name is unique
     * @throws RuntimeException if name already exists
     */
    private void validateLineNameUnique(String lineName) {
        if (existsByLineName(lineName) ) {
            throw new RuntimeException("Line name '" + lineName + "' already exists");
        }
    }
    
    /**
     * Check if line has any machines
     */
    public boolean hasMachines(Long lineId) {
        return lineRepository.hasMachines(lineId);
    }
    
    /**
     * Count machines in a line
     */
    public Long countMachines(Long lineId) {
        return lineRepository.countMachinesByLineId(lineId);
    }
    
    /**
     * Check if line can accept more machines (max 10)
     */
    public boolean canAddMachine(Long lineId) {
        return countMachines(lineId) < 10;
    }
    
    /**
     * Validate line can accept a new machine
     * @throws IllegalStateException if line already has 10 machines
     */
    public void validateCanAddMachine(Long lineId) {
        Long machineCount = countMachines(lineId);
        if (machineCount >= 10) {
            Line line = getLineById(lineId);
            throw new IllegalStateException(
                "Line '" + line.getLineName() + "' already has " + machineCount + " machines. Maximum is 10."
            );
        }
    }
    
    /**
     * Count lines in a specific floor
     */
    public Long countLinesByFloorId(Long floorId) {
        return lineRepository.countByFloorMappingId(floorId);
    }
    
    // ==================== Parent-Child Management ====================
    
    /**
     * Add a machine to a line with validation
     */
    public Line addMachineToLine(Long lineId, Machine machine) {
        Line line = getLineById(lineId);
        
        // Validate 10-machine limit
        validateCanAddMachine(lineId);
        
        // Add machine using entity helper method
        line.addMachine(machine);
        
        return lineRepository.save(line);
    }
    
    /**
     * Remove a machine from a line
     */
    public Line removeMachineFromLine(Long lineId, Machine machine) {
        Line line = getLineById(lineId);
        line.removeMachine(machine);
        return lineRepository.save(line);
    }
    
    /**
     * Get all machines for a specific line
     */
    public List<Machine> getMachinesForLine(Long lineId) {
        Line line = getLineById(lineId);
        return line.getMachines();
    }
    
    // ==================== Statistics ====================
    
    /**
     * Get total number of lines
     */
    public long getTotalCount() {
        return lineRepository.count();
    }
    
    /**
     * Get summary for dashboard
     */
    public String getSummary(Long lineId) {
        Line line = getLineById(lineId);
        Long machineCount = countMachines(lineId);
        return line.getLineName() + " (" + machineCount + " machines, Floor: " + line.getFloorName() + ")";
    }
}