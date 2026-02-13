package com.example.userauth.security;

import com.example.userauth.entity.User;
import com.example.userauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User normalUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        normalUser = new User();
        normalUser.setId(1L);
        normalUser.setUsername("normaluser");
        normalUser.setPasswordHash("hashedpassword");
        normalUser.setIsAdmin(false);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("adminuser");
        adminUser.setPasswordHash("hashedpassword");
        adminUser.setIsAdmin(true);
    }

    @Test
    @DisplayName("Should load normal user by username")
    void loadUserByUsername_NormalUser() {
        when(userRepository.findByUsername("normaluser")).thenReturn(Optional.of(normalUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("normaluser");

        assertNotNull(userDetails);
        assertEquals("normaluser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertFalse(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Should load admin user by username")
    void loadUserByUsername_AdminUser() {
        when(userRepository.findByUsername("adminuser")).thenReturn(Optional.of(adminUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("adminuser");

        assertNotNull(userDetails);
        assertEquals("adminuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void loadUserByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Should return correct password")
    void loadUserByUsername_ReturnsCorrectPassword() {
        when(userRepository.findByUsername("normaluser")).thenReturn(Optional.of(normalUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("normaluser");

        assertEquals("hashedpassword", userDetails.getPassword());
    }

    @Test
    @DisplayName("Should return enabled user")
    void loadUserByUsername_UserEnabled() {
        when(userRepository.findByUsername("normaluser")).thenReturn(Optional.of(normalUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("normaluser");

        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }
}
