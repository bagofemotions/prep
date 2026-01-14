package com.example.smt_management.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.smt_management.dtos.AuthRequest;
import com.example.smt_management.dtos.AuthResponse;
import com.example.smt_management.services.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ViewControllertemp {
	
	private AuthService authService;
//	private APIHandlerService apiHandlerService;
	
	@Autowired
	 public ViewControllertemp(AuthService authService) {
//			 APIHandlerService apiHandlerService) {
		this.authService = authService;
//		this.apiHandlerService = apiHandlerService;
	}

//	  @PostMapping("/logout")
//	  public String logout(HttpServletResponse response) {
//	    Cookie cookie = new Cookie("JWT", "");
//	    cookie.setMaxAge(0);
//	    cookie.setPath("/");
//	    response.addCookie(cookie);
//	    return "redirect:/login";
//	  }
//	  
//	  @PostMapping("/login")
//	  public String htmxLogin(
//	      @RequestParam String username,
//	      @RequestParam String password,
//	      Model model,
//	      HttpServletResponse response) {
//
//	    if (username.isBlank() || password.isBlank()) {
//	      model.addAttribute("error", "Username and password required");
//	      return "fragments/messages :: *";
//	    }
//
//	    try {
//	      AuthResponse auth = authService.authenticate(
//	          new AuthRequest(username, password));
//
//	      Cookie cookie = new Cookie("JWT", auth.getToken());
//	      cookie.setHttpOnly(true);
//	      cookie.setPath("/");
//	      response.addCookie(cookie);
//
//	      model.addAttribute("success", "Login successful");
//	    } catch (Exception e) {
//	      model.addAttribute("error", "Invalid credentials");
//	    }
//
//	    return "fragments/messages :: *";
//	  }
	
    @GetMapping("/login")
    public String getLogin(HttpServletResponse response,  Model model) {
    	model.addAttribute("password", "passed value");
    	model.addAttribute("username", "passed user");
    	return "login";
    }

}
