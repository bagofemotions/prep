package com.example.smt_management.controllers;

import java.util.List;


import com.example.smt_management.entities.User;


//@Controller
public class JsonController {

//    private final JsonService jsonService;
//    private final UserService userService; // To save/load data
//
//    public JsonController(JsonService jsonService, UserService userService) {
//        this.jsonService = jsonService;
//        this.userService = userService;
//    }
//
//    // --- 1. IMPORT JSON (HTMX) ---
//    @PostMapping("/json/upload")
//    public String uploadJson(@RequestParam("file") MultipartFile file, Model model) {
//        try {
//            // Parse
//            List<User> users = jsonService.parseUserJson(file);
//            
//            // Save to DB (Looping for simplicity, batch save is better for large lists)
//            for (User u : users) {
//                userService.saveUser(u);
//            }
//
//            model.addAttribute("type", "success");
//            model.addAttribute("message", "Successfully imported " + users.size() + " users from JSON!");
//            return "fragments/ui :: alertToast";
//
//        } catch (Exception e) {
//            model.addAttribute("type", "danger");
//            model.addAttribute("message", "JSON Import Failed: " + e.getMessage());
//            return "fragments/ui :: alertToast";
//        }
//    }
//
//    // --- 2. EXPORT JSON (Browser Download) ---
//    @GetMapping("/json/download")
//    public ResponseEntity<byte[]> downloadJson() throws Exception {
//        
//        // Fetch real data
//        List<User> allUsers = userService.getAllUsers(org.springframework.data.domain.Pageable.unpaged()).getContent();
//
//        // Convert to JSON String
//        String jsonString = jsonService.generateUserJson(allUsers);
//        byte[] jsonBytes = jsonString.getBytes();
//
//        String filename = "users_backup_" + System.currentTimeMillis() + ".json";
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(jsonBytes);
//    }
}