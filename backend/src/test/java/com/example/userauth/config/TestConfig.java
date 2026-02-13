package com.example.userauth.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = false, securedEnabled = false, jsr250Enabled = false)
public class TestConfig {

    @Bean
    @Primary
    public AppProperties appProperties() {
        AppProperties.CorsProperties cors = new AppProperties.CorsProperties(
            "*",
            "GET,POST,PUT,DELETE",
            "*",
            false
        );
        
        return new AppProperties(null, null, null, cors);
    }

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .anonymous(anonymous -> anonymous.principal("testuser").authorities("ROLE_USER", "ROLE_ADMIN"));
        return http.build();
    }
}
