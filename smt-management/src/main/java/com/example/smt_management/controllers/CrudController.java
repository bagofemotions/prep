package com.example.smt_management.controllers;

import java.util.List;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.smt_management.entities.Floor;
import com.example.smt_management.entities.Line;
import com.example.smt_management.entities.Machine;
import com.example.smt_management.services.FloorService;
import com.example.smt_management.services.LineService;
import com.example.smt_management.services.MachineService;

public class CrudController {

    @Autowired
    private FloorService floorService;
    @Autowired
    private MachineService machineService;
    @Autowired
    private LineService lineService;

   @GetMapping("/floors")
   public String listFloors(@RequestParam(defaultValue = "0") int page, Model model) {
       model.addAttribute("CONTENT_TITLE", "Floor Management");
       model.addAttribute("activeLink", "floors"); // Highlights sidebar

       // Fetch paginated data (Size 10)
       Page<Floor> floorPage = floorService.getAllFloors(PageRequest.of(page, 10));
       model.addAttribute("users", floorPage);

       return "users/table"; // Points to src/main/resources/templates/users/table.html
   }

    @GetMapping("/machines")
   public String listMachines(@RequestParam(defaultValue = "0") int page, Model model) {
       model.addAttribute("CONTENT_TITLE", "Machine Management");
       model.addAttribute("activeLink", "Machines"); // Highlights sidebar

       // Fetch paginated data (Size 10)
       Page<Machine> machinePage = machineService.getAllMachines(PageRequest.of(page, 10));
       model.addAttribute("users", machinePage);

       return "users/table"; // Points to src/main/resources/templates/users/table.html
   }

    @GetMapping("/lines")
   public String listLines(@RequestParam(defaultValue = "0") int page, Model model) {
       model.addAttribute("CONTENT_TITLE", "Line Management");
       model.addAttribute("activeLink", "Lines"); // Highlights sidebar

       // Fetch paginated data (Size 10)
       Page<Line> linPage = lineService.getAllLines(PageRequest.of(page, 10));
       model.addAttribute("users", linPage);

       return "users/table"; // Points to src/main/resources/templates/users/table.html
   }

   @GetMapping("/floors/{id}/lines")
    public String givenFloorlistLines(@RequestParam(defaultValue = "0") Long floorId,@RequestParam(defaultValue = "0") int page, Model model) {
       model.addAttribute("CONTENT_TITLE", "Line Management");
       model.addAttribute("activeLink", "Lines"); // Highlights sidebar

       // Fetch paginated data (Size 10)
       List<Line> linPage = lineService.getLineByFloorMappingId(floorId);
       model.addAttribute("users", linPage);

       return "users/table"; // Points to src/main/resources/templates/users/table.html
   }

    @GetMapping("/line/{id}/machines")
    public String givenlinelistMachines(@RequestParam(defaultValue = "0") Long lineId,@RequestParam(defaultValue = "0") int page, Model model) {
       model.addAttribute("CONTENT_TITLE", "Machine Management");
       model.addAttribute("activeLink", "Lines"); // Highlights sidebar

       // Fetch paginated data (Size 10)
       List<Machine> machinePage = machineService.getMachineByLineMappingId(lineId);
       model.addAttribute("users", machinePage);

       return "users/table"; // Points to src/main/resources/templates/users/table.html
   }

   @PostMapping("/floors")
   public String createFloor(@RequestBody Floor floor) {
       floorService.saveFloor(floor);
       return "redirect:/floors";
   }

   @PostMapping("/lines")
   public String createLine(@RequestBody Line line) {
       lineService.saveLine(line);
       floorService.addLineToFloor(line.getFloorMapping().getId(), line);
       return "redirect:/lines";
   }

    @PostMapping("/machines")
    public String createMachine(@RequestBody Machine machine) {
         machineService.saveMachine(machine);
         lineService.addMachinesToLine(machine.getLineMapping().getId(), machine);
         return "redirect:/machines";
    }

    @DeleteMapping("/floors/{id}")
    public String deleteFloor(@PathVariable Long id) {
        floorService.deleteFloor(id);
        return "redirect:/floors";
    }

    @DeleteMapping("/lines/{id}")
    public String deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return "redirect:/lines";
    }

    @DeleteMapping("/machines/{id}")
    public String deleteMachine(@PathVariable String id) {
        machineService.deleteMachine(id);
        return "redirect:/machines";
    }

    @PutMapping("/floors/{id}")
    public String updateFloor(@RequestParam Long id,@RequestBody Floor floor) {
        if(id == null){
            throw new IllegalArgumentException("Floor ID must not be null for update.");
        }
        if(floor.getId() != null && !floor.getId().equals(id)){
            throw new 
        }
        floorService.saveFloor(floor);
        return "redirect:/floors";
    }

    @PutMapping("/lines/{id}")
    public String updateLine(@RequestParam Long id,@RequestBody Line line) {
        if(id == null){
            throw new IllegalArgumentException("Line ID must not be null for update.");
        }
        if(line.getId() != null && !line.getId().equals(id) && lineService.existsById(line.getId()) == true){
            throw new IllegalArgumentException("Updated Line ID conflicts with an existing Line.");
        }
        if(line.getFloorMapping() != null){
            Floor floor = floorService.getFloorById(line.getFloorMapping().getId());
            line.setFloorMapping(floor);
        }
        lineService.saveLine(line);
        return "redirect:/floors";
    }
   


    
}
