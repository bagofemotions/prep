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
//            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
//            <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
//            background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);

        }

        // Otherwise, standard error page
        model.addAttribute("errorMessage", e.getMessage());
        return "error"; 
    }
}