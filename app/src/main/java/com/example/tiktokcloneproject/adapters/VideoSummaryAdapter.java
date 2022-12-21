package com.example.tiktokcloneproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.activity.VideoActivity;
import com.example.tiktokcloneproject.model.VideoSummary;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class VideoSummaryAdapter extends RecyclerView.Adapter<VideoSummaryAdapter.ViewHolder> {

    private ArrayList<VideoSummary> mData;
    private LayoutInflater mInflater;

    private Context mainContext;

    // data is passed into the constructor
    public VideoSummaryAdapter(Context context, ArrayList<VideoSummary> data) {
        this.mainContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.video_summary_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.viewCount.setText(mData.get(position).getWatchCount().toString());
        setThumbnailImage(holder, mData.get(position).getThumbnailUri());
        holder.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(view.getContext(), VideoActivity.class);
                Bundle bundle =  new Bundle();
                bundle.putString("videoId", mData.get(position).getVideoId());
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });

    }

    public void setThumbnailImage(ViewHolder holder, String thumbnailUri) {
        Log.i("url to get: ", "message: " + thumbnailUri);
        StorageReference photoReference = FirebaseStorage.getInstance().getReferenceFromUrl(thumbnailUri);

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.thumbnail.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView viewCount;
        ImageView thumbnail;
        private ItemClickListener itemClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            viewCount = itemView.findViewById(R.id.view_count);
            thumbnail = itemView.findViewById(R.id.image_thumbnail);
            itemView.setOnClickListener(this);
        }

        // allows clicks events to be caught
        void setOnItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(view,getBindingAdapterPosition());
        }
    }

    // convenience method for getting data at click position
//    String getItem(int id) {
//        return mData[id];
//    }



    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}