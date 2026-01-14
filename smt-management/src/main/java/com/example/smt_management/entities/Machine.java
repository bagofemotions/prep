package com.example.smt_management.entities;

import java.time.LocalDate;

import com.example.smt_management.enums.MachineType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Machine {
	@Id
	private String machineSerial;
	private String model;
	private MachineType type;
	private int yearOfManufacturing;
//	private Year year;
	private String manufacturingCompany;
	@Lob
	private byte[] image;

	@ManyToOne
	@JoinColumn(name="line_id")
	private Line lineMapping;

    public void setLineMapping(Line line) {
		this.lineMapping = line;
	}

}
