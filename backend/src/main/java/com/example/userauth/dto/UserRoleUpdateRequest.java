package com.example.userauth.dto;

import jakarta.validation.constraints.NotNull;

public class UserRoleUpdateRequest {

    @NotNull(message = "isAdmin is required")
    private Boolean isAdmin;

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
