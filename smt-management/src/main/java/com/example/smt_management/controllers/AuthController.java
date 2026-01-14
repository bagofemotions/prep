package com.example.smt_management.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.smt_management.dtos.AuthRequest;
import com.example.smt_management.dtos.AuthResponse;
import com.example.smt_management.services.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@RestController
public class AuthController {
    private final AuthService authService;
    @Autowired
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
    
    
    @PostMapping("/logout")
	  public String logout(HttpServletResponse response) {
	    Cookie cookie = new Cookie("JWT", "");
	    cookie.setMaxAge(0);
	    cookie.setPath("/");
	    response.addCookie(cookie);
	    return "redirect:/login";
	  }
    

    
    @PostMapping("/login")
    public Object login(
        @RequestBody(required = false) AuthRequest body,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String password,
        Model model,
        HttpServletRequest request,
        HttpServletResponse response) {

      boolean isHtmx = "true".equalsIgnoreCase(request.getHeader("HX-Request"))
          || (username != null && password != null);

      AuthRequest authReq = body;
      if (authReq == null && username != null && password != null) {
        authReq = new AuthRequest(username, password);
      }

      if (authReq == null || authReq.getUsername() == null || authReq.getPassword() == null) {
        if (isHtmx) {
          model.addAttribute("error", "Both Username and password are required");
          return "fragments/messages :: *";
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Username and password required"));
      }

      try {
        AuthResponse auth = authService.authenticate(authReq);

        if (isHtmx) {
          Cookie cookie = new Cookie("JWT", auth.getToken());
          cookie.setHttpOnly(true);
          cookie.setPath("/");
          response.addCookie(cookie);
          model.addAttribute("success", "Login successful");
          return "fragments/messages :: *";
        }

        return ResponseEntity.ok(auth);
      } catch (Exception e) {
        if (isHtmx) {
          model.addAttribute("error", "Invalid credentials");
          return "fragments/messages :: *";
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Invalid credentials"));
      }
    }

}
