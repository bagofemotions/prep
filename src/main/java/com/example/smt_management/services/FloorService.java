package com.example.smt_management.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.smt_management.entities.Floor;
import com.example.smt_management.entities.Line;
import com.example.smt_management.repositories.EntitySpecifications;
import com.example.smt_management.repositories.FloorRepository;

/**
 * Enhanced Floor Service with validation, search, and business logic
 */
@Service
@Transactional
public class FloorService {
    
    private final FloorRepository floorRepository;
    
    public FloorService(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
    }
    
    // ==================== CRUD Operations ====================
    
    /**
     * Get all floors with pagination
     */
    public Page<Floor> getAllFloors(Pageable pageable) {
        return floorRepository.findAll(pageable);
    }
    
    /**
     * Get all floors without pagination (for dropdowns)
     */
    public List<Floor> getAllFloorsForDropdown() {
        return floorRepository.findAll();
    }
    
    /**
     * Get floor by ID
     */
    public Floor getFloorById(Long id) {
        return floorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floor not found with ID: " + id));
    }
    
    /**
     * Save new floor or update existing one
     */
    public Floor saveFloor(Floor floor) {
        // Validate floor name uniqueness (for new floors)
        if (floor.getId() == null) {
            validateFloorNameUnique(floor.getName());
        } else {
            // For updates, check if name changed and if new name is unique
            Floor existing = getFloorById(floor.getId());
            if (!existing.getName().equals(floor.getName())) {
                validateFloorNameUnique(floor.getName());
            }
        }
        
        return floorRepository.save(floor);
    }
    
    /**
     * Delete floor with validation
     */
    public void deleteFloor(Long id) {
        Floor floor = getFloorById(id);
        
        // Check if floor has lines
        if (hasLines(id)) {
            Long lineCount = countLines(id);
            throw new RuntimeException(
                "Cannot delete floor '" + floor.getName() + "'. " +
                "It has " + lineCount + " line(s). Please delete all lines first or use cascade delete."
            );
        }
        
        floorRepository.deleteById(id);
    }
    
    /**
     * Force delete floor with all its lines (cascade)
     */
    public void cascadeDeleteFloor(Long id) {
        Floor floor = getFloorById(id);
        floorRepository.delete(floor); // Cascade will delete all lines
    }
    
    // ==================== Search & Filter ====================
    
    /**
     * Search floors by name with pagination
     */
    public Page<Floor> searchFloors(String name, Pageable pageable) {
        Specification<Floor> spec = EntitySpecifications.filterFloors(name);
        return floorRepository.findAll(spec, pageable);
    }
    
    /**
     * Find floor by exact name
     */
    public Optional<Floor> findByName(String name) {
        return floorRepository.findByName(name);
    }
    
    /**
     * Find floor by name (case-insensitive)
     */
    public Optional<Floor> findByNameIgnoreCase(String name) {
        return floorRepository.findByNameIgnoreCase(name);
    }
    
    // ==================== Validation Methods ====================
    
    /**
     * Check if floor name already exists
     */
    public boolean existsByName(String name) {
        return floorRepository.existsByName(name);
    }
    
    /**
     * Validate floor name is unique
     * @throws RuntimeException if name already exists
     */
    private void validateFloorNameUnique(String name) {
        if (existsByName(name)) {
            throw new RuntimeException("Floor name '" + name + "' already exists");
        }
    }
    
    /**
     * Check if floor has any lines
     */
    public boolean hasLines(Long floorId) {
        return floorRepository.hasLines(floorId);
    }
    
    /**
     * Count lines in a floor
     */
    public Long countLines(Long floorId) {
        return floorRepository.countLinesByFloorId(floorId);
    }
    
    /**
     * Check if floor can accept more lines (max 10)
     */
    public boolean canAddLine(Long floorId) {
        return countLines(floorId) < 10;
    }
    
    /**
     * Validate floor can accept a new line
     * @throws IllegalStateException if floor already has 10 lines
     */
    public void validateCanAddLine(Long floorId) {
        Long lineCount = countLines(floorId);
        if (lineCount >= 10) {
            Floor floor = getFloorById(floorId);
            throw new IllegalStateException(
                "Floor '" + floor.getName() + "' already has " + lineCount + " lines. Maximum is 10."
            );
        }
    }
    
    // ==================== Parent-Child Management ====================
    
    /**
     * Add a line to a floor with validation
     */
    public Floor addLineToFloor(Long floorId, Line line) {
        Floor floor = getFloorById(floorId);
        
        // Validate 10-line limit
        validateCanAddLine(floorId);
        
        // Add line using entity helper method
        floor.addLine(line);
        
        return floorRepository.save(floor);
    }
    
    /**
     * Remove a line from a floor
     */
    public Floor removeLineFromFloor(Long floorId, Line line) {
        Floor floor = getFloorById(floorId);
        floor.removeLine(line);
        return floorRepository.save(floor);
    }
    
    /**
     * Get all lines for a specific floor
     */
    public List<Line> getLinesForFloor(Long floorId) {
        Floor floor = getFloorById(floorId);
        return floor.getLines();
    }
    
    // ==================== Statistics ====================
    
    /**
     * Get total number of floors
     */
    public long getTotalCount() {
        return floorRepository.count();
    }
    
    /**
     * Get summary for dashboard
     */
    public String getSummary(Long floorId) {
        Floor floor = getFloorById(floorId);
        Long lineCount = countLines(floorId);
        return floor.getName() + " (" + lineCount + " lines)";
    }
}