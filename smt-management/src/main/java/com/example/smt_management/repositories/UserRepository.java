package com.example.smt_management.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.smt_management.entities.User;


public interface UserRepository extends JpaRepository<User, String> {
//    Optional<User> findByUsername(String username);
//    Boolean existsByUsername(String username);
}
