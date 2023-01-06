package com.example.tiktokcloneproject.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profile implements Serializable {

    private String userId, username,  avatarUri, bio;
    private int followers, following, likes;
    private boolean isPrivate;

    public Profile(String userName) {
        this.username = userName;
    }

    public Profile(String userId, String userName) {
        this.userId = userId;
        this.username = userName;
        followers = following = likes = 0;
        avatarUri = "";
        bio = "Add your bio.";
        isPrivate = false;
//        followers = new ArrayList<>();
//        following = new ArrayList<>();
//        myVideoUri = new ArrayList<>();
//        myFavoriteVideoUri = new ArrayList<>();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", username);
        result.put("avatarName", avatarUri);
        result.put("isPrivate", isPrivate);
        result.put("following", following);
        result.put("followers", followers);
        result.put("likes", likes);
        result.put("bio", bio);
//        result.put("myFavoriteVideoUri", myFavoriteVideoUri);
        return result;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return username;
    }



//    public String getName() {
//        return name;
//    }
//
//    public String getBirthdate() {
//        return birthdate;
//    }
//
//    public String getPhone() {
//        return phone;
//    }

    public String getAvatarUri() {
        return avatarUri;
    }

//    public String getEmail() {
//        return email;
//    }

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
//
//    public int getTotalLikes() {
//        return totalLikes;
//    }
}
