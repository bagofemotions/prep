package com.example.smt_management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smt_management.entities.Line;

@Repository
public interface LineRepository extends JpaRepository<Line, Long> {
    Optional<Line> findByLineName(String lineName);
    List<Line> findByFloorMappingId(Long parentId);


}
