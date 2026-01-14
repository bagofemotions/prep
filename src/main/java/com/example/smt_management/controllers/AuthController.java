package com.example.smt_management.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Simplified Auth Controller - Let Spring Security handle login
 */
@Controller
public class AuthController {

    /**
     * Custom logout handler to clear JWT cookie
     */
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Clear JWT cookie
        Cookie cookie = new Cookie("JWT", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        
        // Let Spring Security handle the rest
        return "redirect:/login?logout";
    }
}