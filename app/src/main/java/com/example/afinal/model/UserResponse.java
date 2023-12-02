package com.example.afinal.model;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("realm")
    private String realm;

    @SerializedName("realmId")
    private String realmId;

    @SerializedName("id")
    private String id;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("enabled")
    private boolean enabled;

    @SerializedName("createdOn")
    private long createdOn;

    @SerializedName("serviceAccount")
    private boolean serviceAccount;

    @SerializedName("username")
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
