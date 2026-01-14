package com.example.smt_management.configs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter
//           , ExternalApiAuthenticationProvider externalApiAuthenticationProvider) 
    ) {
        this.jwtFilter = jwtFilter;

//        this.externalApiAuthenticationProvider = externalApiAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public resources
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login", "/logout", "/error").permitAll()
                        
                        // Dashboard - authenticated users
                        .requestMatchers("/dashboard").authenticated()
                        .requestMatchers("/dashboard-operator").hasRole("OPERATOR")
                        .requestMatchers("/dashboard-admin").hasRole("ADMIN")
                        
                        // Floor management - Admin only for write operations
                        .requestMatchers("/floors/create", "/floors/save", "/floors/edit/**", "/floors/delete/**", "/floors/cascade-delete/**", "/floors/check-delete/**").hasRole("ADMIN")
                        .requestMatchers("/floors", "/floors/**").authenticated()
                        
                        // Line management - Admin only for write operations
                        .requestMatchers("/lines/create", "/lines/save", "/lines/edit/**", "/lines/delete/**", "/lines/cascade-delete/**", "/lines/check-delete/**").hasRole("ADMIN")
                        .requestMatchers("/lines", "/lines/**").authenticated()
                        
                        // Machine management - Admin only for write operations
                        .requestMatchers("/machines/create", "/machines/save", "/machines/edit/**", "/machines/delete/**").hasRole("ADMIN")
                        .requestMatchers("/machines", "/machines/**").authenticated()
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login") // ✅ Changed to avoid conflict
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JWT", "JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // ✅ Changed from STATELESS
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
        
//        return http
//                .cors(Customizer.withDefaults())
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        // Public resources
//                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
//                        .requestMatchers("/login", "/logout", "/error").permitAll()
//                        
//                        // Dashboard - authenticated users
//                        .requestMatchers("/dashboard").authenticated()
//                        .requestMatchers("/dashboard-operator").hasRole("OPERATOR")
//                        .requestMatchers("/dashboard-admin").hasRole("ADMIN")
//                        
//                        // Floor management - Admin only for write operations
//                        .requestMatchers("/floors/create", "/floors/save", "/floors/edit/**", 
//                                       "/floors/delete/**", "/floors/cascade-delete/**", 
//                                       "/floors/check-delete/**").hasRole("ADMIN")
//                        .requestMatchers("/floors", "/floors/**").authenticated()
//                        
//                        // Line management - Admin only for write operations
//                        .requestMatchers("/lines/create", "/lines/save", "/lines/edit/**", 
//                                       "/lines/delete/**", "/lines/cascade-delete/**", 
//                                       "/lines/check-delete/**").hasRole("ADMIN")
//                        .requestMatchers("/lines", "/lines/**").authenticated()
//                        
//                        // Machine management - Admin only for write operations
//                        .requestMatchers("/machines/create", "/machines/save", "/machines/edit/**", 
//                                       "/machines/delete/**").hasRole("ADMIN")
//                        .requestMatchers("/machines", "/machines/**").authenticated()
//                        
//                        // All other requests require authentication
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .loginProcessingUrl("/perform_login")
//                        .defaultSuccessUrl("/dashboard", true)
//                        .failureUrl("/login?error")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/login?logout")
//                        .deleteCookies("JWT", "JSESSIONID")
//                        .permitAll()
//                )
//                .sessionManagement(sess -> sess
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                )
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
    
//    public AuthenticationManager authenticationManager() {
//
//        return new ProviderManager(externalApiAuthenticationProvider);
//
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
//        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:9090"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}