package com.example.smt_management.services;

import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.smt_management.dtos.UserSummaryDto;
import com.example.smt_management.entities.Machine;
import com.example.smt_management.enums.MachineType;
import com.example.smt_management.repositories.MachineRepository;
import com.example.smt_management.repositories.UserSpecification;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;


@Service
public class MachineService {
    private final MachineRepository machineRepository;

   public MachineService(MachineRepository machineRepository) {
       this.machineRepository = machineRepository;
   }

   public Page<Machine> getAllMachines(Pageable pageable) {
       return machineRepository.findAll(pageable);
   }

   public Machine getMachineById(String machineSerial) {
       return machineRepository.findById(machineSerial)
               .orElseThrow(() -> new RuntimeException("Machine not found"));
   }

   public void saveMachine(Machine Machine) {
       machineRepository.save(Machine);
   }

   public void deleteMachine(String machineSerial) {
       machineRepository.deleteById(machineSerial);
   }

   public List<Machine> getMachineByLineMappingId(Long parentId) {
        return machineRepository.findByLineMappingId(parentId);
    }
   

    // to bea handled at frontend
//     public Page<Machine> searchMachines(String model, MachineType type, int yearOfManufacturing, String manufacturingCompany, Pageable pageable){
//        return machineRepository.findAll(UserSpecification.getFilter( model, type, yearOfManufacturing, manufacturingCompany), pageable);
//    }

//    public Map<String, Long> getUserStats() {
//        // Fetches the aggregation (GROUP BY) from DB
//        List<UserSummaryDto> stats = machineRepository.getUserCountByStatus();
       
//        // Convert List to Map for easy display in UI (Stat Cards)
//        return stats.stream().collect(Collectors.toMap(UserSummaryDto::getStatus, UserSummaryDto::getCount));
//    }
}
