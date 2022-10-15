package com.example.tiktokcloneproject;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class User {
    String id;
    String phone;
    String birthdate;
    String email;
    String password;

    User(String id, @Nullable String phone, @Nullable String birthdate, @Nullable String email, String password) {
        this.id = id;
        this.phone = phone;
        this.birthdate = birthdate;
        this.email = email;
        this.password = password;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", id);
        result.put("phone", phone);
        result.put("birthdate", birthdate);
        result.put("email", email);
        result.put("password",password);

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
