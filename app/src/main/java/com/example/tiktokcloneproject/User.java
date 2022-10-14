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
}
