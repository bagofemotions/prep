package com.example.smt_management.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.smt_management.entities.ERole;
import com.example.smt_management.entities.Role;
import com.example.smt_management.repositories.RoleRepository;

/**
 * Initialize default roles and users on application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        Role adminRole = createRoleIfNotExists(ERole.ROLE_ADMIN);
        Role operatorRole = createRoleIfNotExists(ERole.ROLE_OPERATOR);
    }

    private Role createRoleIfNotExists(ERole roleName) {
        return roleRepository.findByName(roleName)
            .orElseGet(() -> {
                Role role = new Role();
                role.setName(roleName);
                Role saved = roleRepository.save(role);
                return saved;
            });
    }
}