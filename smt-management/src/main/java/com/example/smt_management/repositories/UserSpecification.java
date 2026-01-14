package com.example.smt_management.repositories;

//For that advanced Search Bar (filtering by Name AND Role AND Date all at once), simple repository methods aren't enough. We use the Criteria API via a UserSpecification class.

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.smt_management.entities.Floor;
import com.example.smt_management.entities.Line;
import com.example.smt_management.entities.Machine;
import com.example.smt_management.entities.User;
import com.example.smt_management.enums.Direction;
import com.example.smt_management.enums.LaneType;
import com.example.smt_management.enums.MachineType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.Size;

public class UserSpecification {

    /**
     * DYNAMIC FILTER: Builds a WHERE clause based on non-null inputs.
     * Use this in your Service like: userRepository.findAll(getFilter(name, role, status), pageable);
     */
    public static Specification<User> getFilter(String name, String role, String status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Partial Match on Name (Case Insensitive)
            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fullName")), 
                    "%" + name.toLowerCase() + "%"
                ));
            }

            // 2. Exact Match on Role
            if (StringUtils.hasText(role)) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            // 3. Exact Match on Status
            if (StringUtils.hasText(status)) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Machine> getFilter(String model, MachineType type, int yearOfManufacturing, String manufacturingCompany) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Partial Match on Model (Case Insensitive)
            if (StringUtils.hasText(model)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("model")), 
                    "%" + model.toLowerCase() + "%"
                ));
            }

            // 2. Exact Match on Type
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }

            // 3. Exact Match on Year Of Manufacturing
            if (yearOfManufacturing > 0) {
                predicates.add(criteriaBuilder.equal(root.get("yearOfManufacturing"), yearOfManufacturing));
            }

            // 4. Partial Match on Manufacturing Company (Case Insensitive)
            if (StringUtils.hasText(manufacturingCompany)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("manufacturingCompany")), 
                    "%" + manufacturingCompany.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Line> getFilter(String lineName,LaneType lane,Direction direction) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Partial Match on Line Name (Case Insensitive)
            if (StringUtils.hasText(lineName)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("lineName")), 
                    "%" + lineName.toLowerCase() + "%"
                ));
            }

            // 2. Exact Match on Lane
            if (lane != null) {
                predicates.add(criteriaBuilder.equal(root.get("lane"), lane));
            }

            // 3. Exact Match on Direction
            if (direction != null) {
                predicates.add(criteriaBuilder.equal(root.get("direction"), direction));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Floor> getFilter(String name) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Partial Match on Name (Case Insensitive)
            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), 
                    "%" + name.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}