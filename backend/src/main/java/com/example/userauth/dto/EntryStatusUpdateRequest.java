package com.example.userauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class EntryStatusUpdateRequest {
    
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "PENDING|APPROVED|REJECTED", message = "状态必须是 PENDING、APPROVED 或 REJECTED")
    private String status;
    
    public EntryStatusUpdateRequest() {}
    
    public EntryStatusUpdateRequest(String status) {
        this.status = status;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}