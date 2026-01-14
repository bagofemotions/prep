package com.example.smt_management.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.smt_management.entities.User;
import com.example.smt_management.repositories.UserRepository;

// ... imports including BindingResult
   import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

//    private final UserService userService;
//
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    // --- 1. LIST PAGE (The Main Card) ---
//    @GetMapping
//    public String listUsers(@RequestParam(defaultValue = "0") int page, Model model) {
//        model.addAttribute("CONTENT_TITLE", "User Management");
//        model.addAttribute("activeLink", "users"); // Highlights sidebar
//
//        // Fetch paginated data (Size 10)
//        Page<User> userPage = userService.getAllUsers(PageRequest.of(page, 10));
//        model.addAttribute("users", userPage);
//
//        return "users/index"; // Points to src/main/resources/templates/users/index.html
//    }
//
//    // --- 2. SHOW ADD FORM (Returns HTML Fragment for Modal) ---
//    @GetMapping("/create")
//    public String showCreateForm(Model model) {
//        model.addAttribute("user", new User());
//        model.addAttribute("isEdit", false);
//        return "users/form-fragment :: userForm"; // Returns ONLY the form HTML
//    }
//
//    // --- 3. SHOW EDIT FORM (Returns HTML Fragment for Modal) ---
//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable Long id, Model model) {
//        User user = userService.getUserById(id);
//        model.addAttribute("user", user);
//        model.addAttribute("isEdit", true);
//        return "users/form-fragment :: userForm";
//    }
//
//    // --- 4. SAVE (Handle Submit) ---
////    @PostMapping("/save")
////    public String saveUser(@ModelAttribute User user) {
////        userService.saveUser(user);
////        return "redirect:/users"; // Refresh list after save
////    }
////    
//
//    @PostMapping("/save")
//    public String saveUser(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model) {
//        
//        // 1. CHECK STANDARD VALIDATION (Annotations)
//        if (bindingResult.hasErrors()) {
//            // Return the form fragment immediately. 
//            // Thymeleaf will render the field errors inside the inputs.
//            return "users/form-fragment :: userForm"; 
//        }
//
//        // 2. CHECK BUSINESS LOGIC (e.g., Unique Email)
//        // We check this manually because it requires a DB query
//        if (UserRepository.existsByEmail(user.getEmail()) && user.getId() == null) {
//            bindingResult.rejectValue("email", "error.user", "This email is already in use.");
//            return "users/form-fragment :: userForm";
//        }
//
//        userService.saveUser(user);
//        
//        // Success: Redirect to refresh list (or return a success toast)
//        return "redirect:/users"; 
//    }
//
//    // --- 5. DELETE (HTMX Action) ---
//    @DeleteMapping("/delete/{id}")
//    public String deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        // Return to list (or return empty string and remove row via JS)
//        return "redirect:/users"; 
//    }
}