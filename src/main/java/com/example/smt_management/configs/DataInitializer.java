package com.example.smt_management.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.smt_management.entities.ERole;
import com.example.smt_management.entities.Role;
import com.example.smt_management.entities.User;
import com.example.smt_management.repositories.RoleRepository;
import com.example.smt_management.repositories.UserRepository;

/**
 * Initialize default roles and users on application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        Role adminRole = createRoleIfNotExists(ERole.ROLE_ADMIN);
        Role operatorRole = createRoleIfNotExists(ERole.ROLE_OPERATOR);

        // Create default admin user if not exists
        if (!userRepository.existsById("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActive(true);
            admin.addRole(adminRole);
            userRepository.save(admin);
            System.out.println("✅ Default admin user created: admin/admin123");
        }

        // Create default operator user if not exists
        if (!userRepository.existsById("operator")) {
            User operator = new User();
            operator.setUsername("operator");
            operator.setPassword(passwordEncoder.encode("operator123"));
            operator.setActive(true);
            operator.addRole(operatorRole);
            userRepository.save(operator);
            System.out.println("✅ Default operator user created: operator/operator123");
        }
    }

    private Role createRoleIfNotExists(ERole roleName) {
        return roleRepository.findByName(roleName)
            .orElseGet(() -> {
                Role role = new Role();
                role.setName(roleName);
                Role saved = roleRepository.save(role);
                System.out.println("✅ Role created: " + roleName);
                return saved;
            });
    }
}