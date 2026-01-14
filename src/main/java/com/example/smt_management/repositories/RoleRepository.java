package com.example.smt_management.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smt_management.entities.ERole;
import com.example.smt_management.entities.Role;

/**
 * Repository for Role entity
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find role by name
     */
    Optional<Role> findByName(ERole name);
    
    /**
     * Check if role exists by name
     */
    boolean existsByName(ERole name);
}