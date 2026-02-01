package com.example.userauth.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to mark methods that require admin privileges.
 * When applied to a method, only users with admin role can access it.
 * 
 * Usage:
 * @RequireAdmin
 * public ResponseEntity<?> adminOnlyMethod() {
 *     // This method can only be called by admin users
 * }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAdmin {
    
    /**
     * Optional message to include in the access denied exception.
     * 
     * @return the error message
     */
    String message() default "Admin privileges required";
}