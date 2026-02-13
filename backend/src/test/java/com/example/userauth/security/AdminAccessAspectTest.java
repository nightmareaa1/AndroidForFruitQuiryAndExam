package com.example.userauth.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminAccessAspect Tests")
class AdminAccessAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RequireAdmin requireAdmin;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminAccessAspect adminAccessAspect;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);
        lenient().when(methodSignature.getName()).thenReturn("adminMethod");
    }

    @Test
    @DisplayName("Should allow access for admin user")
    void checkAdminAccess_AdminUser() throws Throwable {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
            .when(authentication).getAuthorities();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Object expectedResult = new Object();
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object result = adminAccessAspect.checkAdminAccess(joinPoint, requireAdmin);

        assertEquals(expectedResult, result);
        verify(joinPoint).proceed();
    }

    @Test
    @DisplayName("Should deny access when user is not authenticated")
    void checkAdminAccess_NotAuthenticated() throws Throwable {
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            adminAccessAspect.checkAdminAccess(joinPoint, requireAdmin);
        });

        assertEquals("Authentication required", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    @DisplayName("Should deny access when authentication is null")
    void checkAdminAccess_NullAuthentication() throws Throwable {
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            adminAccessAspect.checkAdminAccess(joinPoint, requireAdmin);
        });

        assertEquals("Authentication required", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    @DisplayName("Should deny access for non-admin user")
    void checkAdminAccess_NonAdminUser() throws Throwable {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
            .when(authentication).getAuthorities();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(requireAdmin.message()).thenReturn("Admin access required");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            adminAccessAspect.checkAdminAccess(joinPoint, requireAdmin);
        });

        assertEquals("Admin access required", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    @DisplayName("Should deny access when user has no authorities")
    void checkAdminAccess_NoAuthorities() throws Throwable {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");
        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(requireAdmin.message()).thenReturn("Admin access required");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            adminAccessAspect.checkAdminAccess(joinPoint, requireAdmin);
        });

        assertEquals("Admin access required", exception.getMessage());
        verify(joinPoint, never()).proceed();
    }

    @Test
    @DisplayName("Should allow access when user has multiple roles including admin")
    void checkAdminAccess_MultipleRoles() throws Throwable {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");
        ArrayList authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        doReturn(authorities).when(authentication).getAuthorities();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Object expectedResult = "success";
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object result = adminAccessAspect.checkAdminAccess(joinPoint, requireAdmin);

        assertEquals(expectedResult, result);
        verify(joinPoint).proceed();
    }
}
