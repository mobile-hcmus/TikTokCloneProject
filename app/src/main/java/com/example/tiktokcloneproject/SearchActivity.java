package com.example.tiktokcloneproject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
//import android.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {


     RecyclerView rcv_users;
     UserAdapter userAdapter;
     SearchView searchView;
     TextView textView;
    final String TAG = "ADD";
     ArrayList <User> userArrayList=new ArrayList<User>();;


     FirebaseFirestore userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);


        userDB = FirebaseFirestore.getInstance();


//        DocumentReference docRef = userDB.collection("test").document("q5oJ1TSUn9n56r3jFSaa");
//        docRef.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//
//                } else { }
//            } else { }
//        });


        userDB.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SearchActivity.this,"da ket noi!2 ",
                                    Toast.LENGTH_LONG).show();




                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists())
                                Toast.makeText(SearchActivity.this,document.getData().toString(),
                                        Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(SearchActivity.this,"khong ton tai",
                                            Toast.LENGTH_LONG).show();

                            };


                            Integer n;
                            n=task.getResult().size();
                            Toast.makeText(SearchActivity.this,"kich thuoc la " +n.toString(),
                                    Toast.LENGTH_LONG).show();
                        } else {

                            Toast.makeText(SearchActivity.this,"Loi ket noi voi Server!2",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });








        rcv_users=(RecyclerView) findViewById(R.id.rcv_users);
        textView= (TextView) findViewById(R.id.txt_v);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rcv_users.setLayoutManager(linearLayoutManager);




//        getData();
        userArrayList.add(new User("aa"));
        userAdapter=new UserAdapter(userArrayList);

        rcv_users.setAdapter(userAdapter);

        RecyclerView.ItemDecoration itemDecoration= new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        rcv_users.addItemDecoration(itemDecoration);


    }

    private void getData() {
//        userDB.collection("users").orderBy("userName", Query.Direction.ASCENDING)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null){
//                    Toast.makeText(SearchActivity.this,"Loi ket noi voi Server!",
//                            Toast.LENGTH_LONG).show();
//                    return;
//                };
//
//                Toast.makeText(SearchActivity.this, value.getDocuments().toString(),
//                        Toast.LENGTH_LONG).show();
//
//                for (DocumentChange dc : value.getDocumentChanges()){
//                        userArrayList.add(dc.getDocument().toObject(User.class));
//
//
//
//
//
//                }
//                userAdapter.notifyDataSetChanged();
//
//
//
//            }
//        });

//                .whereEqualTo("userName", true)

//        CollectionReference cities = userDB.collection("test");

//        Map<String, Object> data1 = new HashMap<>();
//        data1.put("name", "San Francisco");
//        cities.document("SF").set(data1);


        DocumentReference docRef = userDB.collection("test").document("SF");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Toast.makeText(SearchActivity.this,"da ket noi!1",
                            Toast.LENGTH_LONG).show();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Toast.makeText(SearchActivity.this,document.getData().toString(),Toast.LENGTH_LONG).show();




                    } else {
                        Toast.makeText(SearchActivity.this,"khong ton tai",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SearchActivity.this,"khong ket noi den server!1",
                            Toast.LENGTH_LONG).show();
                }
            }
        });




        userDB.collection("test")

                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Integer aa=7;
                            aa=task.getResult().size();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                            };
                            Toast.makeText(SearchActivity.this,"da ket noi!2",
                                    Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(SearchActivity.this,"Loi ket noi voi Server!2",
                            Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        SearchManager searchManager=(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView= (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.getFilter().filter(newText);
                return false;
            }
        });



        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();

    }
}