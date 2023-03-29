package com.mstudio.android.mstory.app.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.adapter.adapter_searchuser;
import com.mstudio.android.mstory.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class search_activity extends AppCompatActivity {
    CardView exit;
    CardView delete_text;
    RecyclerView recy_searchuser;
    adapter_searchuser adapter;
    List<User> mUser;
    EditText editText_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.search_activity);
        recy_searchuser = findViewById(R.id.recy_searchuser);
        editText_search = findViewById(R.id.editext_search);
        delete_text = findViewById(R.id.delete_text);
        exit = findViewById(R.id.exit);
        recy_searchuser.setHasFixedSize(true);
        recy_searchuser.setLayoutManager(new LinearLayoutManager(search_activity.this));
        mUser = new ArrayList<>();
        adapter = new adapter_searchuser(search_activity.this,mUser);
        recy_searchuser.setAdapter(adapter);


        readUser();
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString().toLowerCase());
                if(charSequence.equals("")){
                    delete_text.setVisibility(View.GONE);
                }else {
                    delete_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        exit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void searchUser(String s){
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("user_name").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()){
                    User user = datasnapshot.getValue(User.class);
                    mUser.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }
    private void  readUser(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(editText_search.getText().toString().equals("")){
                    mUser.clear();
                    for(DataSnapshot datasnapshot : snapshot.getChildren()){
                        User user =  datasnapshot.getValue(User.class);
                        mUser.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}
