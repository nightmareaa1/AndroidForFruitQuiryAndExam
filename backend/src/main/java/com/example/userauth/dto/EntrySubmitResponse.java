package com.example.userauth.dto;

public class EntrySubmitResponse {
    private Long entryId;
    private String message;
    
    public EntrySubmitResponse() {}
    
    public EntrySubmitResponse(Long entryId, String message) {
        this.entryId = entryId;
        this.message = message;
    }
    
    public Long getEntryId() {
        return entryId;
    }
    
    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
