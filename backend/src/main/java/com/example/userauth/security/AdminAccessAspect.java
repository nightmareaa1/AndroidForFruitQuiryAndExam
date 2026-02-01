package com.example.userauth.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect to handle @RequireAdmin annotation.
 * Intercepts method calls annotated with @RequireAdmin and verifies admin privileges.
 */
@Aspect
@Component
public class AdminAccessAspect {

    private static final Logger logger = LoggerFactory.getLogger(AdminAccessAspect.class);

    /**
     * Around advice for methods annotated with @RequireAdmin.
     * Checks if the current user has admin privileges before allowing method execution.
     * 
     * @param joinPoint the method being intercepted
     * @param requireAdmin the annotation instance
     * @return the result of the method execution
     * @throws Throwable if method execution fails or access is denied
     */
    @Around("@annotation(requireAdmin)")
    public Object checkAdminAccess(ProceedingJoinPoint joinPoint, RequireAdmin requireAdmin) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Unauthenticated user attempted to access admin-only method: {}", 
                       joinPoint.getSignature().getName());
            throw new AccessDeniedException("Authentication required");
        }
        
        // Check if user has admin role
        boolean hasAdminRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority));
        
        if (!hasAdminRole) {
            logger.warn("User '{}' without admin privileges attempted to access admin-only method: {}", 
                       authentication.getName(), joinPoint.getSignature().getName());
            throw new AccessDeniedException(requireAdmin.message());
        }
        
        logger.debug("Admin user '{}' accessing method: {}", 
                    authentication.getName(), joinPoint.getSignature().getName());
        
        // Proceed with method execution
        return joinPoint.proceed();
    }
}