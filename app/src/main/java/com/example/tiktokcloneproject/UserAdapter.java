package com.example.tiktokcloneproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.userItems> implements Filterable {
    private List<User> listUser;
    private List<User> listUserOlld;



    public UserAdapter(List<User> listUser)
    {

        this.listUser=listUser;
        this.listUserOlld=listUser;
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
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String srtSearch=charSequence.toString();
                if (srtSearch.isEmpty()) {
                    listUser=listUserOlld;

                }
                else {
                    List<User> list=new ArrayList<>();
                    for (User user : listUserOlld){
                        if (user.getId().toLowerCase().contains(srtSearch.toLowerCase())){
                            list.add(user);
                        }
                    }
                    listUser=list;

                }

                FilterResults filterResults=new FilterResults();
                filterResults.values=listUser;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listUser= (List<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
