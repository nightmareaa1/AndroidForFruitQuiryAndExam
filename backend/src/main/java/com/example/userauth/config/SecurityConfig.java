package com.example.userauth.config;

import com.example.userauth.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Production-grade security configuration for JWT-based authentication.
 * Includes HTTPS enforcement, security headers, CORS protection, and password policies.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials}")
    private boolean allowCredentials;

    @Value("${app.security.bcrypt-strength:12}")
    private int bcryptStrength;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Force UTF-8 encoding filter - runs before Spring Security
     */
    private Filter encodingFilter() {
        return new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {}

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                HttpServletRequest req = (HttpServletRequest) request;
                HttpServletResponse res = (HttpServletResponse) response;
                
                // Force UTF-8 encoding for all requests
                req.setCharacterEncoding("UTF-8");
                res.setCharacterEncoding("UTF-8");
                res.setContentType("application/json;charset=UTF-8");
                
                chain.doFilter(req, res);
            }

            @Override
            public void destroy() {}
        };
    }

    /**
     * Password encoder with configurable BCrypt strength for production security.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(bcryptStrength);
    }

    /**
     * CORS configuration for cross-origin requests.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        
        if (allowCredentials) {
            List<String> patterns = origins.stream()
                .map(origin -> origin.trim().equals("*") ? "**" : origin.trim())
                .toList();
            configuration.setAllowedOriginPatterns(patterns);
        } else {
            configuration.setAllowedOrigins(origins);
        }
        
        // Parse allowed methods from comma-separated string
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        configuration.setAllowedMethods(methods);
        
        // Parse allowed headers from comma-separated string
        if ("*".equals(allowedHeaders)) {
            configuration.addAllowedHeader("*");
        } else {
            List<String> headers = Arrays.asList(allowedHeaders.split(","));
            configuration.setAllowedHeaders(headers);
        }
        
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(3600L); // 1 hour preflight cache
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Main security filter chain with production-grade security settings.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS Configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // CSRF Protection (disabled for stateless JWT)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Session Management (stateless for JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Security Headers
            .headers(headers -> headers
                // X-Frame-Options: Prevent clickjacking
                .frameOptions(frameOptions -> frameOptions.deny())
                
                // X-Content-Type-Options: Prevent MIME sniffing
                .contentTypeOptions(contentTypeOptions -> {})
                
                // HSTS: Force HTTPS
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000) // 1 year
                    .includeSubDomains(true)
                    .preload(true)
                )
                
                // Referrer Policy: Control referrer information
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            )
            
            // Request Authorization
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/fruit/query").permitAll()
                .requestMatchers("/api/files/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/info").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Admin-only endpoints
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/evaluation-models").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/evaluation-models/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/evaluation-models/**").hasRole("ADMIN")
                .requestMatchers("/api/competitions/*/export").hasRole("ADMIN")
                
                // Authenticated endpoints (all users can read evaluation models)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/evaluation-models/**").authenticated()
                .requestMatchers("/api/competitions/**").authenticated()
                .requestMatchers("/api/ratings/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Add JWT authentication filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(encodingFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }
}