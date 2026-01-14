package com.example.smt_management.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "floors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Floor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Floor name is required")
    @Size(min = 2, max = 100, message = "Floor name must be between 2 and 100 characters")
    private String name;
    
    // FIXED: mappedBy should reference the field name in Line entity, not "id"
    @OneToMany(mappedBy = "floorMapping", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Line> lines = new ArrayList<>();
    
    /**
     * Add a line to this floor with validation
     * @throws IllegalStateException if floor already has 10 lines
     */
    public void addLine(Line line) {
        if (lines.size() >= 10) {
            throw new IllegalStateException("A Floor can have at most 10 lines.");
        }
        lines.add(line);
        line.setFloorMapping(this);
    }
    
    /**
     * Remove a line from this floor
     */
    public void removeLine(Line line) {
        lines.remove(line);
        line.setFloorMapping(null);
    }
    
    /**
     * Get all lines for this floor
     */
    public List<Line> getLines() {
        return lines;
    }
    
    /**
     * Check if floor has any lines (for delete confirmation)
     */
    public boolean hasLines() {
        return lines != null && !lines.isEmpty();
    }
    
    /**
     * Get count of lines
     */
    public int getLineCount() {
        return lines != null ? lines.size() : 0;
    }
}