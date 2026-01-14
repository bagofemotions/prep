package com.example.smt_management.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.smt_management.dtos.AuthRequest;
import com.example.smt_management.dtos.AuthResponse;
import com.example.smt_management.entities.ERole;
import com.example.smt_management.entities.Role;
import com.example.smt_management.entities.User;
import com.example.smt_management.repositories.RoleRepository;
import com.example.smt_management.repositories.UserRepository;
import com.example.smt_management.util.JwtUtil;


@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;
    
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
	public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository,
			RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return new AuthResponse(token);
    }

}
