package com.example.smt_management.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smt_management.entities.Machine;

@Repository
public interface MachineRepository extends JpaRepository<Machine, String> {
    List<Machine> findByLineMappingId(Long parentId);

}
