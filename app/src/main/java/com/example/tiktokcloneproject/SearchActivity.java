package com.example.tiktokcloneproject;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
//import android.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktokcloneproject.adapters.UserAdapter;
import com.example.tiktokcloneproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {


     RecyclerView rcv_users;
     UserAdapter userAdapter;
     SearchView searchView;

    final String TAG = "ADD";
     ArrayList <User> userArrayList=new ArrayList<User>();;


     FirebaseFirestore userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);






        userArrayList.clear();
        userDB = FirebaseFirestore.getInstance();



        rcv_users=(RecyclerView) findViewById(R.id.rcv_users);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rcv_users.setLayoutManager(linearLayoutManager);

        String key="";

        getData(key);
        userAdapter=new UserAdapter(this,userArrayList);
        rcv_users.setAdapter(userAdapter);
        RecyclerView.ItemDecoration itemDecoration= new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        rcv_users.addItemDecoration(itemDecoration);


    }




    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (userAdapter!=null){
            userAdapter.release();
        }
    }

    private void getData(String key) {
        userArrayList.clear();
//        userDB.collection("users").orderBy("userName", Query.Direction.ASCENDING)
//                .whereEqualTo("userName", true)
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
//                }
//                userAdapter.notifyDataSetChanged();
//
//            }
//        });


        userDB.collection("users")
                .orderBy("userName")
                .startAt(key)
                .endAt(key+"\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SearchActivity.this,"da ket noi!2 ",
                                    Toast.LENGTH_LONG).show();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userArrayList.add(new User(document.getString("userId"),document.getString("userName")));



                                Toast.makeText(SearchActivity.this,document.getString("userName"),
                                        Toast.LENGTH_LONG).show();
                            };
                            userAdapter.notifyDataSetChanged();
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
//                userAdapter.getFilter().filter(query);

//                userArrayList.clear();
                getData(query);
                userAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(SearchActivity.this,newText,
//                        Toast.LENGTH_LONG).show();
//                userAdapter.getFilter().filter(newText);
//                userArrayList.clear();
                getData(newText);
                userAdapter.notifyDataSetChanged();


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

    private boolean checkIsFollowing(ArrayList<String> userIDFollowingList, String anotherUserID) {
        return userIDFollowingList.contains(anotherUserID);
    }
}