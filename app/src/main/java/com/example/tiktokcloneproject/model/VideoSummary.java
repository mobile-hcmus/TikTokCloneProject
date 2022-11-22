package com.example.tiktokcloneproject.model;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class VideoSummary {
    private String videoId;
    private Bitmap thumbnailUri;
    private int watchCount;

    public VideoSummary(String videoId, Bitmap thumbnailUri) {
        this.videoId = videoId;
        this.thumbnailUri = thumbnailUri;
        watchCount = 0;
    }

    public String getVideoId() {
        return videoId;
    }

    public Bitmap getThumbnailUri() {
        return thumbnailUri;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("videoId", videoId);
        result.put("thumbnailUri", thumbnailUri);
        result.put("watchCount", watchCount);

        return result;
    }
}
