package com.example.tiktokcloneproject.adapters;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktokcloneproject.CommentActivity;
import com.example.tiktokcloneproject.ProfileActivity;
import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.model.Video;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<Video> videos;
    Context context;

    public VideoAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    public void addVideoObject(Video video) {
        this.videos.add(video);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_container, parent, false ));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        holder.setVideoObjects(videos.get(position));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        ImageView imvAvatar, imvPause;
        TextView txvDescription, txvTitle;
        TextView tvComment, tvFavorites;
        ProgressBar pgbWait;


        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            txvTitle = itemView.findViewById(R.id.txvTitle);
            txvDescription = itemView.findViewById(R.id.txvDescription);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvFavorites = itemView.findViewById(R.id.tvFavorites);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
            imvPause = itemView.findViewById(R.id.imvPause);
            pgbWait = itemView.findViewById(R.id.pgbWait);

        }

        @SuppressLint("ClickableViewAccessibility")
        public void setVideoObjects(final Video videoObjects) {
            txvTitle.setText("@" + videoObjects.getUsername());
            txvDescription.setText(videoObjects.getDescription());
            videoView.setVideoPath(videoObjects.getVideoUri());

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference download = storageRef.child("/user_avatars").child(videoObjects.getAuthorId());
//                        StorageReference download = storageRef.child(userId.toString());
            long MAX_BYTE = 1024*1024;
            download.getBytes(MAX_BYTE)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                            imvAvatar.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Do nothing
                        }
                    });

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    pgbWait.setVisibility(View.GONE);
                    mediaPlayer.start();
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    Toast.makeText(videoView.getContext(), "Finish", Toast.LENGTH_SHORT).show();
                }
            });
            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(videoView.isPlaying()) {
                        videoView.pause();
                        imvPause.setVisibility(View.VISIBLE);
                        return false;
                    }
                    else {
                        imvPause.setVisibility(View.GONE);
                        videoView.start();
                        return false;
                    }
                }
            });

           tvComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("videoId", videoObjects.getVideoId());
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
                }
            });
        }
    } // class ViewHolder


}// class adapter
