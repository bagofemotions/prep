package com.example.smt_management.entities;

import java.util.ArrayList;
import java.util.List;

import com.example.smt_management.enums.Direction;
import com.example.smt_management.enums.LaneType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Line {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Line name is required")
    @Size(min = 2, max = 100, message = "Line name must be between 2 and 100 characters")
    private String lineName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Lane type is required")
    private LaneType lane;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Direction is required")
    private Direction direction;
    
    // FIXED: Proper @ManyToOne relationship with Floor
    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    @NotNull(message = "Floor mapping is required")
    private Floor floorMapping;
    
    // FIXED: mappedBy should reference the field name in Machine entity, not "machineSerial"
    @OneToMany(mappedBy = "lineMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Machine> machines = new ArrayList<>();
    
    /**
     * Set the parent floor for this line
     */
    public void setFloorMapping(Floor floor) {
        this.floorMapping = floor;
    }
    
    /**
     * Add a machine to this line with validation
     * @throws IllegalStateException if line already has 10 machines
     */
    public void addMachine(Machine machine) {
        if (machines.size() >= 10) {
            throw new IllegalStateException("A Line can have at most 10 machines.");
        }
        machines.add(machine);
        machine.setLineMapping(this);
    }
    
    /**
     * Remove a machine from this line
     */
    public void removeMachine(Machine machine) {
        machines.remove(machine);
        machine.setLineMapping(null);
    }
    
    /**
     * Get all machines for this line
     */
    public List<Machine> getMachines() {
        return machines;
    }
    
    /**
     * Check if line has any machines (for delete confirmation)
     */
    public boolean hasMachines() {
        return machines != null && !machines.isEmpty();
    }
    
    /**
     * Get count of machines
     */
    public int getMachineCount() {
        return machines != null ? machines.size() : 0;
    }
    
    /**
     * Get parent floor ID
     */
    public Long getFloorId() {
        return floorMapping != null ? floorMapping.getId() : null;
    }
    
    /**
     * Get parent floor name
     */
    public String getFloorName() {
        return floorMapping != null ? floorMapping.getName() : null;
    }
}