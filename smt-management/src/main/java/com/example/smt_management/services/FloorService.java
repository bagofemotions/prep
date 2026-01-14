package com.example.smt_management.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.smt_management.entities.Floor;
import com.example.smt_management.entities.Line;
import com.example.smt_management.repositories.FloorRepository;

import jakarta.transaction.Transactional;

@Service
public class FloorService{
    private final FloorRepository floorRepository;

   public FloorService(FloorRepository floorRepository) {
       this.floorRepository = floorRepository;
   }

   public Page<Floor> getAllFloors(Pageable pageable) {
       return floorRepository.findAll(pageable);
   }

   public Floor getFloorById(Long id) {
       return floorRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Floor not found"));
   }

   public void saveFloor(Floor floor) {
       floorRepository.save(floor);
   }

   public void deleteFloor(Long id) {
       floorRepository.deleteById(id);
   }

    @Transactional
    public Floor addLineToFloor(Long floorId, Line line) {
        Floor parent = floorRepository.findById(floorId)
                .orElseThrow(() -> new RuntimeException("Floor not found"));

        if (parent.getLines().size() >= 10) {
            throw new IllegalStateException("Cannot add more than 10 lines to a floor.");
        }

        parent.addLine(line);
        return floorRepository.save(parent);
    }

    public boolean existsById(Long id) {
        return floorRepository.existsById(id);
    }
    
}
