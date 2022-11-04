package com.example.tiktokcloneproject;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Video {
//    private static final AtomicInteger count = new AtomicInteger(0);
    private String Id;
    private String Url, authorId, description;
    private ArrayList<String> hashtag, commentList;
    private int totalLikes, privacy; //(0: public, 1: private, 2: follower)
    private boolean downloadable;

    public Video(String id, String url, String authorId, String description) {
        Url = url;
        this.authorId = authorId;
        this.description = description;
        this.Id = id;
        hashtag = new ArrayList<String>();
        commentList = new ArrayList<String>();
        totalLikes = 0;
        privacy = 0;
        downloadable = true;
    }

    public String getId() {
        return Id;
    }

    public String getUrl() {
        return Url;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getHashtag() {
        return hashtag;
    }

    public ArrayList<String> getCommentList() {
        return commentList;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public int getPrivacy() {
        return privacy;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Id", Id);
        result.put("Url", Url);
        result.put("authorId", authorId);
        result.put("description", description);
        result.put("hashtag", hashtag);
        result.put("commentList", commentList);
        result.put("totalLikes", totalLikes);
        result.put("privacy", privacy);
        result.put("downloadable", downloadable);

        return result;
    }
}
