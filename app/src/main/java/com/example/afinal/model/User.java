package com.example.afinal.model;

public class User {
    public String username, password, email, token;
    public User () {
        this.username = "";
        this.password = "";
        this.email = "";
        this.token = "";
    }
    public User (String username, String password, String email, String token) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}
