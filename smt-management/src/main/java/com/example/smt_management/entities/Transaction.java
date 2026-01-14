package com.example.smt_management.entities;


import java.time.LocalDateTime;

// <--- CRITICAL: Use jakarta, not javax
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Use jakarta.persistence.Column
    @Column(nullable = false)
    private Double amount;

    private String description;

    private LocalDateTime date;

    // --- THE RELATIONSHIP (Many Transactions belong to One User) ---
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "user_id", nullable = false) // Foreign Key in 'transactions' table
    private User user;
}
