package com.example.smt_management.entities;

import com.example.smt_management.enums.MachineType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "machines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Machine {
    
    @Id
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Machine serial is required")
    @Size(min = 3, max = 50, message = "Machine serial must be between 3 and 50 characters")
    private String machineSerial;
    
    @Column(nullable = false)
    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 100, message = "Model must be between 2 and 100 characters")
    private String model;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Machine type is required")
    private MachineType type;
    
    @Column(nullable = false)
    @NotNull(message = "Year of manufacturing is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer yearOfManufacturing;
    
    @Column(nullable = false)
    @NotBlank(message = "Manufacturing company is required")
    @Size(min = 2, max = 100, message = "Manufacturing company must be between 2 and 100 characters")
    private String manufacturingCompany;
    
    @Lob
    @Column(name = "image", columnDefinition = "bytea")
    private byte[] image;
    
    // FIXED: Proper @ManyToOne relationship with Line
    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    @NotNull(message = "Line mapping is required")
    private Line lineMapping;
    
    /**
     * Set the parent line for this machine
     */
    public void setLineMapping(Line line) {
        this.lineMapping = line;
    }
    
    /**
     * Get parent line ID
     */
    public Long getLineId() {
        return lineMapping != null ? lineMapping.getId() : null;
    }
    
    /**
     * Get parent line name
     */
    public String getLineName() {
        return lineMapping != null ? lineMapping.getLineName() : null;
    }
    
    /**
     * Get parent floor ID (through line)
     */
    public Long getFloorId() {
        return lineMapping != null ? lineMapping.getFloorId() : null;
    }
    
    /**
     * Get parent floor name (through line)
     */
    public String getFloorName() {
        return lineMapping != null ? lineMapping.getFloorName() : null;
    }
    
    /**
     * Check if machine has image
     */
    public boolean hasImage() {
        return image != null && image.length > 0;
    }
}