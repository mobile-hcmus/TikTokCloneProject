package com.example.tiktokcloneproject.model;

import com.google.firebase.database.FirebaseDatabase;

public class Notification {
    public static final String FOLLOW = "0";
    public static final String COMMENT = "1";
    public static final String LIKE = "2";

    private String fromUsername;
    private String action;
    private long timestamp;

    public Notification() {
    }

    public Notification(String fromUser, String action) {
        this.fromUsername = fromUser;
        this.action = action;
        timestamp = System.currentTimeMillis();
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public String getAction() {
        return action;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static void pushNotification(String fromUsername, String toUserId, String action) {
        FirebaseDatabase.getInstance()
                        .getReference()
                        .child(toUserId)
                        .push()
                        .setValue(new Notification(fromUsername, action)
                        );
    }
}
