package com.example.smt_management.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "User Name must be between 2 and 50 characters")
    private String username;
    
    @Column(nullable = false)
    @NotBlank(message = "password is required")
    @Size(min = 8, max = 16, message = "password must be between 8 and 16 characters")
    private String password;
    
//    private Role role;
//    
//
//    @Pattern(regexp = "Active|Inactive", message = "Status must be Active or Inactive")
//    private String status;
}