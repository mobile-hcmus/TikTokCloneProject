package com.example.tiktokcloneproject.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Comment {
    private String commentId, videoId, authorId, content;
    private int totalLikes, totalReplies;
    private ArrayList<String> replyIds;

    public Comment(String commentId, String videoId, String authorId,  String content) {
        this.commentId = commentId;
        this.videoId = videoId;
        this.authorId = authorId;
        this.content = content;
        totalLikes = 0;
        totalReplies = 0;
        replyIds = new ArrayList<>();
    }

    public Comment() {
    }

    public String getCommentId() {
        return commentId;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getAuthorId() {
        return authorId;
    }


    public String getContent() {
        return content;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public int getTotalReplies() {
        return totalReplies;
    }

    public ArrayList<String> getReplyIds() {
        return replyIds;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("commentId", commentId);
        result.put("videoId", videoId);
        result.put("authorId", authorId);
        result.put("totalLikes", totalLikes);
        result.put("totalReplies", totalReplies);
        result.put("replyIds", replyIds);
        result.put("content", content);
        return result;
    }

}
