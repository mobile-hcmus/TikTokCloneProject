package com.example.tiktokcloneproject.model;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Video {
    private String videoId, videoUri, authorId, authorAvatarUri, description, username;
    private int totalLikes, totalComments;
    private ArrayList<String> hashtags;

    public Video() {
    }

    public Video(String videoId, String videoUri, String authorId, String username, String authorAvatarUri, String description) {
        this.videoId = videoId;
        this.videoUri = videoUri;
        this.authorId = authorId;
        this.username = username;
        this.authorAvatarUri = authorAvatarUri;
        this.description = description;
        totalLikes = totalComments = 0;
        hashtags = new ArrayList<>();
    }

    public Video(String videoId, String videoUri, String authorId, String authorAvatarUri, String description, String username, int totalLikes, int totalComments) {
        this.videoId = videoId;
        this.videoUri = videoUri;
        this.authorId = authorId;
        this.authorAvatarUri = authorAvatarUri;
        this.description = description;
        this.username = username;
        this.totalLikes = totalLikes;
        this.totalComments = totalComments;
    }

    public String getUsername() {
        return username;
    }

    public Video(String videoId, String videoUri, String authorId, String username, String authorAvatarUri, String description, ArrayList<String> hashtags) {
        this.videoId = videoId;
        this.videoUri = videoUri;
        this.authorId = authorId;
        this.username = username;
        this.authorAvatarUri = authorAvatarUri;
        this.description = description;
        this.hashtags = hashtags;
        totalLikes = totalComments = 0;
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

    public String getAuthorAvatarUri() {
        return authorAvatarUri;
    }

    public String getDescription() {
        return description;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("videoId", videoId);
        result.put("videoUri", videoUri);
        result.put("authorId", authorId);
        result.put("username", username);
        result.put("authorAvatarUri", authorAvatarUri);
        result.put("description", description);
        result.put("hashtags", hashtags);
        result.put("totalComments", totalComments);
        result.put("totalLikes", totalLikes);

        return result;
    }
}
