package com.example.smt_management.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.smt_management.logging.AppLog;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom authentication provider that validates credentials against external API
 */
@Component
public class ExternalApiAuthenticationProvider implements AuthenticationProvider {

    @Value("${external.api.url:http://localhost:8090/api/login}")
    private String externalApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            // Call external API to validate credentials and get role
            String role = validateWithExternalApi(username, password);
            
            if (role == null) {
                throw new BadCredentialsException("Invalid username or password from external API");
            }

            // Get user authorities based on the role from API
            List<GrantedAuthority> authorities = getUserAuthorities(role);

            // Create authenticated token
            return new UsernamePasswordAuthenticationToken(username, password, authorities);
            
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error calling external API: " + e.getMessage());
            throw new BadCredentialsException("External API authentication failed: " + e.getMessage());
        }
    }

//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        String username = authentication.getName();
//        String password = authentication.getCredentials().toString();
//
//        try {
//            // Call external API to validate credentials
//            boolean isValid = validateWithExternalApi(username, password);
//            
//            if (!isValid) {
//                throw new BadCredentialsException("Invalid username or password from external API");
//            }
//
//            // Get user roles from external API (or assign default roles)
//            List<GrantedAuthority> authorities = getUserAuthorities(username);
//
//            // Create authenticated token
//            return new UsernamePasswordAuthenticationToken(username, password, authorities);
//            
//        } catch (BadCredentialsException e) {
//            throw e;
//        } catch (Exception e) {
//            System.err.println("Error calling external API: " + e.getMessage());
//            throw new BadCredentialsException("External API authentication failed: " + e.getMessage());
//        }
//    }
//    

    /**
     * Call external API to validate username and password, returns role if valid
     */
    private String validateWithExternalApi(String username, String password) {
        try {
            // Prepare request body
            String jsonRequest = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", 
                                              username, password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);

            // call external API
            ResponseEntity<String> response = restTemplate.exchange(
                externalApiUrl,
                HttpMethod.POST,
                request,
                String.class
            );

            // Parse response
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                // Check if authentication was successful
                if (jsonNode.has("status")) {
                    String status = jsonNode.get("status").asText();
                    if ("ok".equalsIgnoreCase(status) || "success".equalsIgnoreCase(status)) {
                        // Return the role from the response
                        if (jsonNode.has("role")) {
                            return jsonNode.get("role").asText();
                        }
                    }
                    if("fail".equalsIgnoreCase(status)) {
                    	if(jsonNode.has("message")) {
                    		String messageString = jsonNode.get("message").asText();
                    		AppLog.error("User Authentication failure: " + messageString, new BadCredentialsException(messageString));
                    	}
                    }
                }
                
            }

            return null; // Invalid credentials

        } catch (Exception e) {
            System.err.println("External API call failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


//    /**
//     * Call external API to validate username and password
//     */
//    private boolean validateWithExternalApi(String username, String password) {
//        try {
//            // Prepare request body
//            String jsonRequest = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", 
//                                              username, password);
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<String> request = new HttpEntity<>(jsonRequest, headers);
//
//            // call external API
//            ResponseEntity<String> response = restTemplate.exchange(
//                externalApiUrl,
//                HttpMethod.POST,
//                request,
//                String.class
//            );
//
//            // Parse response
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                JsonNode jsonNode = objectMapper.readTree(response.getBody());
//                
//                // Check if authentication was successful
//                if (jsonNode.has("status")) {
//                    String status = jsonNode.get("status").asText();
//                    return "success".equalsIgnoreCase(status) || "ok".equalsIgnoreCase(status);
//                }
//                
//                // If we get 200 OK, assume it's valid
//                return true;
//            }
//
//            return false;
//
//        } catch (Exception e) {
//            System.err.println("External API call failed: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//    }

//    /**
//     * Get user authorities/roles from external API or assign default roles
//     */
//    private List<GrantedAuthority> getUserAuthorities(String username) {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        
//        try {
//            // Option 1: Get roles from external API
//            String roleApiUrl = externalApiUrl.replace("/validate", "/roles?username=" + username);
//            ResponseEntity<String> response = restTemplate.getForEntity(roleApiUrl, String.class);
//            
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                JsonNode jsonNode = objectMapper.readTree(response.getBody());
//                
//                if (jsonNode.has("role")) {
//                    jsonNode.get("role").forEach(roleNode -> {
//                        String role = roleNode.asText();
//                        // Ensure role has ROLE_ prefix
//                        if (!role.startsWith("ROLE_")) {
//                            role = "ROLE_" + role.toUpperCase();
//                        }
//                        authorities.add(new SimpleGrantedAuthority(role));
//                    });
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Could not fetch roles from external API: " + e.getMessage());
//        }
//        
//        // Option 2: Assign default roles based on username
//        if (authorities.isEmpty()) {
//            if ("admin".equalsIgnoreCase(username)) {
//                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//            } else if ("operator".equalsIgnoreCase(username)) {
//                authorities.add(new SimpleGrantedAuthority("ROLE_OPERATOR"));
//            } else {
//                // Default role for all authenticated users
//                authorities.add(new SimpleGrantedAuthority("ROLE_OPERATOR"));
//            }
//        }
//
//        return authorities;
//    }

    /**
     * Get user authorities/roles based on the role string from API
     */
    private List<GrantedAuthority> getUserAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        if (role != null && !role.trim().isEmpty()) {
            // Use the role from external API
            String authority = "ROLE_" + role.toUpperCase();
            authorities.add(new SimpleGrantedAuthority(authority));
        } else {
            // Fallback to default role if no role provided
            authorities.add(new SimpleGrantedAuthority("ROLE_OPERATOR"));
        }

        return authorities;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
