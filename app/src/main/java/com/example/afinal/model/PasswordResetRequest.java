package com.example.afinal.model;

public class PasswordResetRequest {
    private String type;
    private String value;
    private boolean temporary;

    public PasswordResetRequest(String type, String value, boolean temporary) {
        this.type = type;
        this.value = value;
        this.temporary = temporary;
    }

}
