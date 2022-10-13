package com.example.tiktokcloneproject;

public class User {
    private int id;
    private String phone;
    private String birthdate;
    private String email;
    private String password;

    User(int id, String phone, String birthdate, String email, String password) {
        this.id = id;
        this.phone = phone;
        this.birthdate = birthdate;
        this.email = email;
        this.password = password;
    }
}
