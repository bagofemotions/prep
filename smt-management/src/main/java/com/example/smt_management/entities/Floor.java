package com.example.smt_management.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Floor {
	@Id
	public Long id;
	@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
	public String name;
	@OneToMany(mappedBy = "id", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Line> lines = new ArrayList<>();

	public void addLine(Line line) {
        if (lines.size() >= 10) {
            throw new IllegalStateException("A Floor can have at most 10 lines.");
        }
        lines.add(line);
        line.setFloorMapping(this);
    }

	public void removeLine(Line line) {
        lines.remove(line);
        line.setFloorMapping(null);
    }

	public List<Line> getLines() {
		return lines;
	}
}
