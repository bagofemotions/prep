package com.example.smt_management.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle standard Java Runtime Exceptions (like the one we throw in CsvService)
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, HttpServletRequest request, Model model) {
        
        // If the request came from HTMX (Ajax), return a Toast Fragment
        if (request.getHeader("HX-Request") != null) {
            model.addAttribute("type", "danger");
            model.addAttribute("message", e.getMessage());
            return "fragments/ui :: alertToast";
        }

        // Otherwise, standard error page
        model.addAttribute("errorMessage", e.getMessage());
        return "error"; 
    }
}