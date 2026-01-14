package com.example.smt_management.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.example.smt_management.entities.Floor;
import com.example.smt_management.entities.Line;
import com.example.smt_management.entities.Machine;
import com.example.smt_management.enums.Direction;
import com.example.smt_management.enums.LaneType;
import com.example.smt_management.enums.MachineType;

import jakarta.persistence.criteria.Predicate;

/**
 * Dynamic filter specifications for JPA Criteria API
 * Used for advanced search and filtering
 */
public class EntitySpecifications {

    /**
     * Floor filter specification
     */
    public static Specification<Floor> filterFloors(String name) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by name (case-insensitive partial match)
            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Line filter specification
     */
    public static Specification<Line> filterLines(
            String lineName, 
            LaneType lane, 
            Direction direction,
            Long floorId) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by line name (case-insensitive partial match)
            if (StringUtils.hasText(lineName)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("lineName")),
                    "%" + lineName.toLowerCase() + "%"
                ));
            }
            
            // Filter by lane type
            if (lane != null) {
                predicates.add(criteriaBuilder.equal(root.get("lane"), lane));
            }
            
            // Filter by direction
            if (direction != null) {
                predicates.add(criteriaBuilder.equal(root.get("direction"), direction));
            }
            
            // Filter by floor
            if (floorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("floorMapping").get("id"), floorId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Machine filter specification
     */
    public static Specification<Machine> filterMachines(
            String model, 
            MachineType type, 
            Integer yearOfManufacturing, 
            String manufacturingCompany,
            Long lineId) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by model (case-insensitive partial match)
            if (StringUtils.hasText(model)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("model")),
                    "%" + model.toLowerCase() + "%"
                ));
            }
            
            // Filter by machine type
            if (type != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            
            // Filter by year of manufacturing
            if (yearOfManufacturing != null && yearOfManufacturing > 0) {
                predicates.add(criteriaBuilder.equal(
                    root.get("yearOfManufacturing"), 
                    yearOfManufacturing
                ));
            }
            
            // Filter by manufacturing company (case-insensitive partial match)
            if (StringUtils.hasText(manufacturingCompany)) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("manufacturingCompany")),
                    "%" + manufacturingCompany.toLowerCase() + "%"
                ));
            }
            
            // Filter by line
            if (lineId != null) {
                predicates.add(criteriaBuilder.equal(root.get("lineMapping").get("id"), lineId));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}