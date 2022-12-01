package com.example.tiktokcloneproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.model.Comment;
import com.example.tiktokcloneproject.model.Notification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CommentAdapter extends ArrayAdapter<Comment> {
    private ArrayList<Comment> comments;
    private Context context;

    public CommentAdapter(@NonNull Context context, int resource, ArrayList<Comment> comments) {
        super(context, resource, comments);
        this.comments = comments;
        this.context = context;
    }

    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.layout_row_comment, null);

        }

        ImageView imvAvatarInComment = (ImageView) row.findViewById(R.id.imvAvatarInComment);
        TextView txvUsernameInComment = (TextView) row.findViewById(R.id.txvUsernameInComment);
        TextView txvComment = (TextView) row.findViewById(R.id.txvComment);
        TextView txvTotalLikeComment = (TextView) row.findViewById(R.id.txvTotalLikeComment);

        loadAvatar(comments.get(position).getAuthorId(), imvAvatarInComment);
        txvUsernameInComment.setText(comments.get(position).getAuthorId());
        txvComment.setText(comments.get(position).getContent());
        txvTotalLikeComment.setText(comments.get(position).getTotalLikes() + "");

        return (row);
    }

    private void loadAvatar(String authorId, ImageView imv) {
        StorageReference download = FirebaseStorage.getInstance().getReference().child("/user_avatars").child(authorId);
//                        StorageReference download = storageRef.child(userId.toString());
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
//                    }
//                    else { }
//                }
//                else { }
//            });
    }
}
