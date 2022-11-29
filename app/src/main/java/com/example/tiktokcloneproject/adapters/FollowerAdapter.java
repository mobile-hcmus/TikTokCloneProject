package com.example.tiktokcloneproject.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tiktokcloneproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class FollowerAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> followerIdList;
    private  ArrayList<String> followerUserNameList;

    public FollowerAdapter(Context context, ArrayList<String> followerIdList, ArrayList<String> followerUserNameList) {
        this.inflater = LayoutInflater.from(context);
        this.followerIdList = followerIdList;
        this.followerUserNameList = followerUserNameList;
    }

    @Override
    public int getCount() {
        return followerIdList.size();
    }

    @Override
    public Object getItem(int i) {
        return followerIdList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        // Đổ dữ liệu vào biến View, view này chính là những gì nằm trong item_name.xml
        view = inflater.inflate(R.layout.layout_follower_item, null);

        FirebaseStorage storage;
        StorageReference storageReference;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        StorageReference download = storageReference.child("/user_avatars").child(followerIdList.get(i));

        ImageView imvAvatar = view.findViewById(R.id.imv_follower_avatar);

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
        TextView tvUserName = (TextView) view.findViewById(R.id.tv_followers_userMame);
        tvUserName.setText(followerUserNameList.get(i));
        TextView tvRemove = (TextView) view.findViewById(R.id.tv_remove_follower);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder((Activity) view.getContext());

                alertDialog.setTitle("Remove this follower");
                alertDialog.setMessage("\"" + followerUserNameList.get(i) + "\" will no longer follow you and won't be notified that you removed them" );

                alertDialog.setPositiveButton(
                        "Remove",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("profiles").document(currentUser.getUid())
                                        .collection("followers").document(followerIdList.get(i))
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("remove", "follower removed");
                                                Toast.makeText(context, "Follower removed", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("remove", "fail",e);
                                            }
                                        });
                                db.collection("profiles").document(followerIdList.get(i))
                                        .collection("following").document(currentUser.getUid())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("remove", "follower removed");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("remove", "fail",e);
                                            }
                                        });

                            }
                        }
                );
                 alertDialog.show();


            }
        });

        return view;
    }
}
