package com.example.tiktokcloneproject;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoObject> videoObjects;

    public VideoAdapter(List<VideoObject> videoObjects) {
        this.videoObjects = videoObjects;
    }

    public void addVideoObjects(VideoObject videoObject) {
        this.videoObjects.add(videoObject);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_container, parent, false ));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoObjects(videoObjects.get(position));
    }

    @Override
    public int getItemCount() {
        return videoObjects.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        TextView txvDescription, txvTitle;
        ProgressBar pgbWait;


        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            txvTitle = itemView.findViewById(R.id.txvTitle);
            txvDescription = itemView.findViewById(R.id.txvDescription);
            pgbWait = itemView.findViewById(R.id.pgbWait);

        }

        @SuppressLint("ClickableViewAccessibility")
        public void setVideoObjects(final VideoObject videoObjects) {
            txvTitle.setText(videoObjects.getAuthorId());
            txvDescription.setText(videoObjects.getDescription());
            videoView.setVideoPath(videoObjects.getUrl());

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    pgbWait.setVisibility(View.GONE);
                    mediaPlayer.start();
                }
            });

            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(videoView.isPlaying()) {
                        videoView.pause();
                        return false;
                    }
                    else {
                        videoView.start();
                        return false;
                    }
                }
            });
        }
    } // class ViewHolder


}// class adapter
