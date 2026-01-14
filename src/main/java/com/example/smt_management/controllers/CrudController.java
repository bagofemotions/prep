package com.example.smt_management.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.smt_management.entities.Floor;
import com.example.smt_management.entities.Line;
import com.example.smt_management.entities.Machine;
import com.example.smt_management.enums.Direction;
import com.example.smt_management.enums.LaneType;
import com.example.smt_management.enums.MachineType;
import com.example.smt_management.services.FloorService;
import com.example.smt_management.services.LineService;
import com.example.smt_management.services.MachineService;

import jakarta.validation.Valid;

/**
 * CRUD Controller - Handles all Floor, Line, and Machine operations
 * Role-based access control:
 * - Admin: Full CRUD access
 * - Operator: Read-only access
 */
@Controller
public class CrudController {
    
    @Autowired
    private FloorService floorService;
    
    @Autowired
    private LineService lineService;
    
    @Autowired
    private MachineService machineService;
    
    // ==================== FLOOR ENDPOINTS ====================
    
    /**
     * List all floors with pagination and search
     */
    @GetMapping("/floors")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public String listFloors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Floor> floorPage;
        
        if (search != null && !search.trim().isEmpty()) {
            floorPage = floorService.searchFloors(search, pageable);
        } else {
            floorPage = floorService.getAllFloors(pageable);
        }
        
        model.addAttribute("floors", floorPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", floorPage.getTotalPages());
        model.addAttribute("totalItems", floorPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("CONTENT_TITLE", "Floor Management");
        model.addAttribute("activeLink", "floors");
        
        return "floors/list";
    }
    
    /**
     * Show create floor form
     */
    @GetMapping("/floors/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createFloorForm(Model model) {
        model.addAttribute("floor", new Floor());
        model.addAttribute("CONTENT_TITLE", "Create Floor");
        model.addAttribute("activeLink", "floors");
        return "floors/form";
    }
    
    /**
     * Show edit floor form
     */
    @GetMapping("/floors/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editFloorForm(@PathVariable Long id, Model model) {
        Floor floor = floorService.getFloorById(id);
        model.addAttribute("floor", floor);
        model.addAttribute("CONTENT_TITLE", "Edit Floor");
        model.addAttribute("activeLink", "floors");
        return "floors/form";
    }
    
    /**
     * Save floor (create or update)
     */
    @PostMapping("/floors/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveFloor(
            @Valid @ModelAttribute Floor floor,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation error: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/floors";
        }
        
        try {
            floorService.saveFloor(floor);
            redirectAttributes.addFlashAttribute("success", "Floor saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/floors";
    }
    
    /**
     * Check if floor can be deleted (has children)
     */
    @GetMapping("/floors/check-delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String checkDeleteFloor(@PathVariable Long id, Model model) {
        Floor floor = floorService.getFloorById(id);
        boolean hasLines = floorService.hasLines(id);
        Long lineCount = floorService.countLines(id);
        
        model.addAttribute("entity", floor);
        model.addAttribute("entityType", "Floor");
        model.addAttribute("entityName", floor.getName());
        model.addAttribute("hasChildren", hasLines);
        model.addAttribute("childCount", lineCount);
        model.addAttribute("childType", "lines");
        model.addAttribute("deleteUrl", "/floors/delete/" + id);
        model.addAttribute("cascadeDeleteUrl", "/floors/cascade-delete/" + id);
        
        return "fragments/delete-confirm :: deleteModal";
    }
    
    /**
     * Delete floor (without children)
     */
    @PostMapping("/floors/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteFloor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            floorService.deleteFloor(id);
            redirectAttributes.addFlashAttribute("success", "Floor deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/floors";
    }
    
    /**
     * Cascade delete floor (with all children)
     */
    @PostMapping("/floors/cascade-delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String cascadeDeleteFloor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            floorService.cascadeDeleteFloor(id);
            redirectAttributes.addFlashAttribute("success", "Floor and all its lines deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/floors";
    }
    
    // ==================== LINE ENDPOINTS ====================
    
    /**
     * List all lines with pagination and search
     */
    @GetMapping("/lines")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public String listLines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long floorId,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("lineName").ascending());
        Page<Line> linePage;
        
        if (search != null && !search.trim().isEmpty() || floorId != null) {
            linePage = lineService.searchLines(search, null, null, floorId, pageable);
        } else {
            linePage = lineService.getAllLines(pageable);
        }
        
        // Get all floors for filter dropdown
        List<Floor> floors = floorService.getAllFloorsForDropdown();
        
        model.addAttribute("lines", linePage.getContent());
        model.addAttribute("floors", floors);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", linePage.getTotalPages());
        model.addAttribute("totalItems", linePage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("selectedFloorId", floorId);
        model.addAttribute("CONTENT_TITLE", "Line Management");
        model.addAttribute("activeLink", "lines");
        
        return "lines/list";
    }
    
    /**
     * Show create line form
     */
    @GetMapping("/lines/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createLineForm(Model model) {
        List<Floor> floors = floorService.getAllFloorsForDropdown();
        
        model.addAttribute("line", new Line());
        model.addAttribute("floors", floors);
        model.addAttribute("laneTypes", LaneType.values());
        model.addAttribute("directions", Direction.values());
        model.addAttribute("CONTENT_TITLE", "Create Line");
        model.addAttribute("activeLink", "lines");
        
        return "lines/form";
    }
    
    /**
     * Show edit line form
     */
    @GetMapping("/lines/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editLineForm(@PathVariable Long id, Model model) {
        Line line = lineService.getLineById(id);
        List<Floor> floors = floorService.getAllFloorsForDropdown();
        
        model.addAttribute("line", line);
        model.addAttribute("floors", floors);
        model.addAttribute("laneTypes", LaneType.values());
        model.addAttribute("directions", Direction.values());
        model.addAttribute("CONTENT_TITLE", "Edit Line");
        model.addAttribute("activeLink", "lines");
        
        return "lines/form";
    }
    
    /**
     * Save line (create or update)
     */
    @PostMapping("/lines/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveLine(
            @Valid @ModelAttribute Line line,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation error: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/lines";
        }
        
        try {
            lineService.saveLine(line);
            redirectAttributes.addFlashAttribute("success", "Line saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/lines";
    }
    
    /**
     * Check if line can be deleted (has children)
     */
    @GetMapping("/lines/check-delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String checkDeleteLine(@PathVariable Long id, Model model) {
        Line line = lineService.getLineById(id);
        boolean hasMachines = lineService.hasMachines(id);
        Long machineCount = lineService.countMachines(id);
        
        model.addAttribute("entity", line);
        model.addAttribute("entityType", "Line");
        model.addAttribute("entityName", line.getLineName());
        model.addAttribute("hasChildren", hasMachines);
        model.addAttribute("childCount", machineCount);
        model.addAttribute("childType", "machines");
        model.addAttribute("deleteUrl", "/lines/delete/" + id);
        model.addAttribute("cascadeDeleteUrl", "/lines/cascade-delete/" + id);
        
        return "fragments/delete-confirm :: deleteModal";
    }
    
    /**
     * Delete line (without children)
     */
    @PostMapping("/lines/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteLine(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lineService.deleteLine(id);
            redirectAttributes.addFlashAttribute("success", "Line deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/lines";
    }
    
    /**
     * Cascade delete line (with all children)
     */
    @PostMapping("/lines/cascade-delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String cascadeDeleteLine(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lineService.cascadeDeleteLine(id);
            redirectAttributes.addFlashAttribute("success", "Line and all its machines deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/lines";
    }
    
    // ==================== MACHINE ENDPOINTS ====================
    
    /**
     * List all machines with pagination and search
     */
    @GetMapping("/machines")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public String listMachines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long lineId,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("machineSerial").ascending());
        Page<Machine> machinePage;
        
        if (search != null && !search.trim().isEmpty() || lineId != null) {
            machinePage = machineService.searchMachines(search, null, null, null, lineId, pageable);
        } else {
            machinePage = machineService.getAllMachines(pageable);
        }
        
        // Get all lines for filter dropdown
        List<Line> lines = lineService.getAllLinesForDropdown();
        
        model.addAttribute("machines", machinePage.getContent());
        model.addAttribute("lines", lines);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", machinePage.getTotalPages());
        model.addAttribute("totalItems", machinePage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("selectedLineId", lineId);
        model.addAttribute("CONTENT_TITLE", "Machine Management");
        model.addAttribute("activeLink", "machines");
        
        return "machines/list";
    }
    
    /**
     * Show create machine form
     */
    @GetMapping("/machines/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createMachineForm(Model model) {
        List<Line> lines = lineService.getAllLinesForDropdown();
        
        model.addAttribute("machine", new Machine());
        model.addAttribute("lines", lines);
        model.addAttribute("machineTypes", MachineType.values());
        model.addAttribute("CONTENT_TITLE", "Create Machine");
        model.addAttribute("activeLink", "machines");
        
        return "machines/form";
    }
    
    /**
     * Show edit machine form
     */
    @GetMapping("/machines/edit/{machineSerial}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editMachineForm(@PathVariable String machineSerial, Model model) {
        Machine machine = machineService.getMachineBySerial(machineSerial);
        List<Line> lines = lineService.getAllLinesForDropdown();
        
        model.addAttribute("machine", machine);
        model.addAttribute("lines", lines);
        model.addAttribute("machineTypes", MachineType.values());
        model.addAttribute("CONTENT_TITLE", "Edit Machine");
        model.addAttribute("activeLink", "machines");
        
        return "machines/form";
    }
    
    /**
     * Save machine (create or update)
     */
    @PostMapping("/machines/save")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveMachine(
            @Valid @ModelAttribute Machine machine,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation error: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/machines";
        }
        
        try {
            machineService.saveMachine(machine);
            redirectAttributes.addFlashAttribute("success", "Machine saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/machines";
    }
    
    /**
     * Delete machine
     */
    @PostMapping("/machines/delete/{machineSerial}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteMachine(@PathVariable String machineSerial, RedirectAttributes redirectAttributes) {
        try {
            machineService.deleteMachine(machineSerial);
            redirectAttributes.addFlashAttribute("success", "Machine deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/machines";
    }
}