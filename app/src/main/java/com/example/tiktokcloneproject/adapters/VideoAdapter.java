package com.example.tiktokcloneproject.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktokcloneproject.activity.CommentActivity;
import com.example.tiktokcloneproject.activity.DeleteVideoSettingActivity;
import com.example.tiktokcloneproject.activity.MainActivity;
import com.example.tiktokcloneproject.activity.ProfileActivity;
import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.activity.SettingsAndPrivacyActivity;
import com.example.tiktokcloneproject.activity.VideoActivity;
import com.example.tiktokcloneproject.model.Video;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<Video> videos;
    private Context context;
    private static FirebaseUser user = null;

    public VideoAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    public static void setUser(FirebaseUser user) {
        VideoAdapter.user = user;
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

    public void showNiceDialogBox(Context context, @Nullable String title, @Nullable String message) {
        if(title == null) {
            title = context.getString(R.string.request_account_title);
        }
        if(message == null) {
            message = context.getString(R.string.request_account_message);
        }
        try {
            //CAUTION: sometimes TITLE and DESCRIPTION include HTML markers
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
            myBuilder.setIcon(R.drawable.splash_background)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    })
                    .setPositiveButton("Sign up/Sign in", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichOne) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }}) //setNegativeButton
                    .show();
        }
        catch (Exception e) { Log.e("Error DialogBox", e.getMessage() ); }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        VideoView videoView;
        ImageView imvAvatar, imvPause, imvMore;
        TextView txvDescription, tvTitle;
        TextView tvComment, tvFavorites;
        ProgressBar pgbWait;
        String authorId;
        String videoId;
        int totalComments;
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        FirebaseUser user = mauth.getCurrentUser();

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            txvDescription = itemView.findViewById(R.id.txvDescription);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvFavorites = itemView.findViewById(R.id.tvFavorites);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
            imvPause = itemView.findViewById(R.id.imvPause);
            pgbWait = itemView.findViewById(R.id.pgbWait);
            imvMore = itemView.findViewById(R.id.imvMore);

            imvAvatar.setOnClickListener(this);
            tvTitle.setOnClickListener(this);
            tvComment.setOnClickListener(this);
            imvMore.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(view.getId() == imvAvatar.getId()) {
                moveToProfile(videoView.getContext(), authorId);
            }
            if(view.getId() == tvTitle.getId()) {
                moveToProfile(videoView.getContext(), authorId);
            }
            if(view.getId() == tvComment.getId()) {
                if(user == null) {
                    showNiceDialogBox(view.getContext(), null, null);
                    return;
                }
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("videoId", videoId);
                bundle.putString("authorId", authorId);
                bundle.putInt("totalComments", totalComments);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
            if (view.getId() == imvMore.getId()) {
                if (authorId.equals(user.getUid())) {
                    Intent intent = new Intent(view.getContext(), DeleteVideoSettingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("videoId", videoId);
                    bundle.putString("authorId", authorId);
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
                else {
                    moveToProfile(videoView.getContext(), authorId);
                }
            }
        }


        @SuppressLint("ClickableViewAccessibility")
        public void setVideoObjects(final Video videoObjects) {
            tvTitle.setText("@" + videoObjects.getUsername());
            txvDescription.setText(videoObjects.getDescription());
            tvComment.setText(String.valueOf(videoObjects.getTotalComments()));
           videoView.setVideoPath(videoObjects.getVideoUri());

            authorId = videoObjects.getAuthorId();
            videoId = videoObjects.getVideoId();
            totalComments = videoObjects.getTotalComments();

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

            showAvt(imvAvatar, videoObjects.getAuthorId());
        }

        private void moveToProfile(Context context, String authorId) {
            Intent intent=new Intent(context, ProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", authorId);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }

        private void showAvt(ImageView imv, String authorId) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference download = storageRef.child("/user_avatars").child(authorId);

            long MAX_BYTE = 1024*1024;
            download.getBytes(MAX_BYTE)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                            imv.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Do nothing
                        }
                    });
        }


    } // class ViewHolder




}// class adapter
