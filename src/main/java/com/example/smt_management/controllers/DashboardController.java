package com.example.smt_management.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.smt_management.services.FloorService;
import com.example.smt_management.services.LineService;
import com.example.smt_management.services.MachineService;

/**
 * Dashboard Controller - Handles dashboard views for Admin and Operator
 */
@Controller
public class DashboardController {

    @Autowired
    private FloorService floorService;

    @Autowired
    private LineService lineService;

    @Autowired
    private MachineService machineService;

    /**
     * Login page - FIXED: Removed model attribute that was causing the error
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Default root redirect to dashboard
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    /**
     * Main dashboard - redirects based on role
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        // Check if user is admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_ADMIN"));

        if (isAdmin) {
            return adminDashboard(model);
        } else {
            return operatorDashboard(model);
        }
    }

    /**
     * Admin Dashboard - Full CRUD access
     */
    @GetMapping("/dashboard-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model) {
        // Add statistics
        model.addAttribute("totalFloors", floorService.getTotalCount());
        model.addAttribute("totalLines", lineService.getTotalCount());
        model.addAttribute("totalMachines", machineService.getTotalCount());

        // Add page metadata
        model.addAttribute("CONTENT_TITLE", "Admin Dashboard");
        model.addAttribute("activeLink", "dashboard");
        model.addAttribute("userRole", "ADMIN");

        return "dashboard";
    }

    /**
     * Operator Dashboard - Read-only access
     */
    @GetMapping("/dashboard-operator")
    @PreAuthorize("hasRole('OPERATOR')")
    public String operatorDashboard(Model model) {
        // Add statistics
        model.addAttribute("totalFloors", floorService.getTotalCount());
        model.addAttribute("totalLines", lineService.getTotalCount());
        model.addAttribute("totalMachines", machineService.getTotalCount());

        // Add page metadata
        model.addAttribute("CONTENT_TITLE", "Operator Dashboard");
        model.addAttribute("activeLink", "dashboard");
        model.addAttribute("userRole", "OPERATOR");

        return "dashboard-operator";
    }
}