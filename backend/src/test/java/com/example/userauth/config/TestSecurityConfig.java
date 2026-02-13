package com.example.userauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@TestConfiguration
public class TestSecurityConfig {
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Disable security for tests to focus on controller logic
    http
      .authorizeRequests().anyRequest().permitAll()
      .and().csrf().disable();
    return http.build();
  }

  // Global test authentication filter to simulate an admin user for all requests
  @Bean
  public FilterRegistrationBean<TestAuthFilter> testAuthFilterRegistration() {
    FilterRegistrationBean<TestAuthFilter> registration = new FilterRegistrationBean<>(new TestAuthFilter());
    registration.setOrder(0);
    return registration;
  }

  public static class TestAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
          "testuser",
          null,
          Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
      );
      SecurityContextHolder.getContext().setAuthentication(auth);
      filterChain.doFilter(request, response);
    }
  }
}
