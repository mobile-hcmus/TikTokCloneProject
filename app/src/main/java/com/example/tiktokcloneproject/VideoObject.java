package com.example.tiktokcloneproject;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class VideoObject {
    private static final AtomicInteger count = new AtomicInteger(0);
    private int Id;
    private String Url, authorId, description;
    private ArrayList<String> hashtag, commentList;
    private int totalLikes, privacy; //(0: public, 1: private, 2: follower)
    private boolean downloadable;

    public VideoObject(String url, String authorId, String description) {
        Url = url;
        this.authorId = authorId;
        this.description = description;
        this.Id = count.incrementAndGet();
        hashtag = new ArrayList<String>();
        commentList = new ArrayList<String>();
        totalLikes = 0;
        privacy = 0;
        downloadable = true;
    }

    public int getId() {
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
}
