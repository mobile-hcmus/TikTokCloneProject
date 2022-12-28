package com.example.tiktokcloneproject.model;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Video {
    private String videoId, videoUri, authorId, description, username;
    private int totalLikes, totalComments;

    public Video() {
    }

    public Video(String videoId, String videoUri, String authorId, String username, String description) {
        this.videoId = videoId;
        this.videoUri = videoUri;
        this.authorId = authorId;
        this.username = username;
        this.description = description;
        totalLikes = totalComments = 0;
    }

    public Video(String videoId, String videoUri, String authorId, String description, String username, int totalLikes, int totalComments) {
        this.videoId = videoId;
        this.videoUri = videoUri;
        this.authorId = authorId;
        this.description = description;
        this.username = username;
        this.totalLikes = totalLikes;
        this.totalComments = totalComments;
    }

    public String getUsername() {
        return username;
    }


    public String getVideoId() {
        return videoId;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) { this.totalLikes = totalLikes; }

    public int getTotalComments() {
        return totalComments;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("videoId", videoId);
        result.put("videoUri", videoUri);
        result.put("authorId", authorId);
        result.put("username", username);
        result.put("description", description);
        result.put("totalComments", totalComments);
        result.put("totalLikes", totalLikes);

        return result;
    }
}
