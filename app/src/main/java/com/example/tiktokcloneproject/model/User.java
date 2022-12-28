package com.example.tiktokcloneproject.model;

import android.net.Uri;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {

    private String userId, username, birthdate, phone, avatarUri, email;
//    private ArrayList<String> followers, following, myVideoUri, myFavoriteVideoUri;
    private boolean isPrivate;
    List<String> followers;
//    private int totalLikes;

    public User(String userName) {
        this.username = userName;
    }

    public User(String userId, String userName) {
        this.userId = userId;
        this.username = userName;
    }

    public User(String userId, String userName, String phone, String email) {
        this.userId = userId;
        this.username = userName;
        this.phone = phone;
        this.email = email;
        this.birthdate = this.avatarUri = "";
//        followers = new ArrayList<>();
//        following = new ArrayList<>();
//        myVideoUri = new ArrayList<>();
//        myFavoriteVideoUri = new ArrayList<>();
        isPrivate = false;
//        totalLikes = 0;
    }

    public User(String userId, String userName, String phone, String email, List<String> followers) {
        this.userId = userId;
        this.username = userName;
        this.phone = phone;
        this.email = email;
        this.birthdate = this.avatarUri = "";
        this.followers = followers;
//        followers = new ArrayList<>();
//        following = new ArrayList<>();
//        myVideoUri = new ArrayList<>();
//        myFavoriteVideoUri = new ArrayList<>();
        isPrivate = false;
//        totalLikes = 0;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", username);
        result.put("birthdate", birthdate);
        result.put("avatarrUri", avatarUri);
        result.put("email", email);
        result.put("isPrivate", isPrivate);
        result.put("phone", phone);
//        result.put("name", name);
//        result.put("followers", followers);
//        result.put("following", following);
//        result.put("myVideoUri", myVideoUri);
//        result.put("myFavoriteVideoUri", myFavoriteVideoUri);
//        result.put("totalLikes", totalLikes);
        return result;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return username;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public String getEmail() {
        return email;
    }

//    public ArrayList<String> getFollowers() {
//        return followers;
//    }
//
//    public ArrayList<String> getFollowing() {
//        return following;
//    }
//
//    public ArrayList<String> getMyVideoUri() {
//        return myVideoUri;
//    }
//
//    public ArrayList<String> getMyFavoriteVideoUri() {
//        return myFavoriteVideoUri;
//    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public List<String> getFollowers() {
        return followers;
    }

    //    public int getTotalLikes() {
//        return totalLikes;
//    }
}
