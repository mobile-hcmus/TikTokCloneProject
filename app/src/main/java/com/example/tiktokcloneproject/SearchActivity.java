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

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {


    private RecyclerView rcv_users;
    private UserAdapter userAdapter;
    private SearchView searchView;


    private FirebaseFirestore userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        userDB = FirebaseFirestore.getInstance();

        rcv_users=(RecyclerView) findViewById(R.id.rcv_users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rcv_users.setLayoutManager(linearLayoutManager);

        userAdapter=new UserAdapter(getListUser());
        rcv_users.setAdapter(userAdapter);
        RecyclerView.ItemDecoration itemDecoration= new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        rcv_users.addItemDecoration(itemDecoration);


    }

    private List<User> getListUser() {
        List <User> list = new ArrayList<>();

        list.add(new User("aa","12","ADa","ada","ada"));
        list.add(new User("bb","12","ADa","ada","ada"));
        list.add(new User("cc","12","ADa","ada","ada"));
        list.add(new User("dd","12","ADa","ada","ada"));
        list.add(new User("aa","12","ADa","ada","ada"));


        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        SearchManager searchManager=(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView= (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.getMaxWidth(Integer.MAX_VALUE);
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