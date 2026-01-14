package com.example.smt_management.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.smt_management.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    /**
     * Find user by username (already covered by findById since username is the ID)
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Check if username exists
     */
    Boolean existsByUsername(String username);
    
    /**
     * Check by ID (username)
     */
    default boolean existsById(String username) {
        return existsByUsername(username);
    }	
}