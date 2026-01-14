package com.example.smt_management.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import com.example.smt_management.entities.Line;
import com.example.smt_management.entities.Machine;
import com.example.smt_management.repositories.LineRepository;

import jakarta.transaction.Transactional;

@Service
public class LineService {
    private final LineRepository lineRepository;

   public LineService(LineRepository lineRepository) {
       this.lineRepository = lineRepository;
   }

   public Page<Line> getAllLines(Pageable pageable) {
       return lineRepository.findAll(pageable);
   }

   public Line getLineById(Long id) {
       return lineRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Line not found"));
   }

   public void saveLine(Line line) {
       lineRepository.save(line);
   }

   public void deleteLine(Long id) {
       lineRepository.deleteById(id);
   }

    @Transactional
    public Line addMachinesToLine(Long lineId, Machine machine) {
        Line parent = lineRepository.findById(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found"));

        if (parent.getMachines().size() >= 10) {
            throw new IllegalStateException("Cannot add more than 10 machines to a line.");
        }

        parent.addMachine(machine);
        return lineRepository.save(parent);
    }

    public List<Line> getLineByFloorMappingId(Long parentId) {
        return lineRepository.findByFloorMappingId(parentId);
    }

    public boolean existsById(Long id) {
        return lineRepository.existsById(id);
    }
    
}
