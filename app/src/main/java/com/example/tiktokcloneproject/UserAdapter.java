package com.example.tiktokcloneproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.userItems> {
    private List<User> listUser;
    public UserAdapter(List<User> listUser)
    {
        this.listUser=listUser;
    }



    @NonNull
    @Override
    public userItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,parent,false);

        return new userItems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull userItems holder, int position) {
        User user= listUser.get(position);
        if (user ==null) {
            return;
        }
        holder.text_Username.setText(user.getId());

    }

    @Override
    public int getItemCount() {
        if (listUser != null)
        { return listUser.size();
        }
        return 0;
    }

    public class userItems extends RecyclerView.ViewHolder{
        private TextView text_Username;


        public userItems(@NonNull View itemView) {
            super(itemView);
            text_Username=(TextView) itemView.findViewById(R.id.text_Username);
        }
    }

}
