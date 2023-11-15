package com.example.afinal;

public class User {
    public String username, password, email;
    public User () {
        this.username = "";
        this.password = "";
        this.email = "";
    }
    public User (String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
