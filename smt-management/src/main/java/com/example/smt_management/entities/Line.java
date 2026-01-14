package com.example.smt_management.entities;

import java.util.ArrayList;
import java.util.List;

import com.example.smt_management.enums.Direction;
import com.example.smt_management.enums.LaneType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Line {
	@Id
	private Long id;
	@Size(min = 2, max = 100, message = " Line Name must be between 2 and 100 characters")
	private String lineName;
    @Enumerated(EnumType.ORDINAL)
	private LaneType lane;
    @Enumerated(EnumType.ORDINAL)
	private Direction direction;
	
	@ManyToOne
	@JoinColumn(name="floor_id")
	Floor floorMapping;
	
	@OneToMany(mappedBy = "machineSerial", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Machine> machines = new ArrayList<>();

	public void setFloorMapping(Floor floor) {
		this.floorMapping = floor;
	}

	public void addMachine(Machine machine) {
        if (machines.size() >= 10) {
            throw new IllegalStateException("A Line can have at most 10 machines.");
        }
        machines.add(machine);
        machine.setLineMapping(this);
    }

	public void removeMachine(Machine machine) {
        machines.remove(machine);
        machine.setLineMapping(null);
    }

	public List<Machine> getMachines() {
		return machines;
	}
}
