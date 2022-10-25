package com.example.tiktokcloneproject;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class User {

    String userName;
    String phone;

    public User(String userName) {
        this.userName = userName;
    }

    String birthdate;
    String email;
    String password;

    User(String userName, @Nullable String phone, @Nullable String birthdate, @Nullable String email, @Nullable String password) {
        this.userName = userName;
        this.phone = phone;
        this.birthdate = birthdate;
        this.email = email;
        this.password = password;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("phone", phone);
        result.put("birthdate", birthdate);
        result.put("email", email);
        result.put("password",password);

        return result;
    }

    public Map<String, Object> toMapProfile() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", getuserName());
        result.put("following", 0);
        result.put("followers", 0);
        result.put("totalLikes", 0);
        result.put("isPrivate", false);

        return result;
    }

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
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
