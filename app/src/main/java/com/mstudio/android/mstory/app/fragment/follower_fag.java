package com.mstudio.android.mstory.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.search_activity;
import com.mstudio.android.mstory.app.adapter.adapter_searchuser;
import com.mstudio.android.mstory.app.model.Post;
import com.mstudio.android.mstory.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class follower_fag extends Fragment {
    private static follower_fag instance;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    adapter_searchuser adapter_user;
    RecyclerView recy_folower;
    adapter_searchuser adapter;
    List<User> mUser;
    List<String> idlist;
    boolean isloadfirst;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int overallXScroll = 0;
    private int mCurrentIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.follower_frag, container, false);
        instance=this;
        String publisher = getActivity().getIntent().getStringExtra("publisher");
        recy_folower=view.findViewById(R.id.recy_follower);
        recy_folower.setHasFixedSize(true);
        recy_folower.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUser = new ArrayList<>();
        adapter = new adapter_searchuser(getActivity(),mUser);
        recy_folower.setAdapter(adapter);
        idlist = new ArrayList<>();
        getFollower();
        return view;
    }
    private void getFollower(){
        String publisher = getActivity().getIntent().getStringExtra("publisher");
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("follow").child(publisher).child("follower");
        rf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                idlist.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()){

                    idlist.add(datasnapshot.getKey());
                }
                showuser();
            }


            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
    private void showuser() {
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference("Users");
        rf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mUser.clear();
                for(DataSnapshot datasnapshot : snapshot.getChildren()){
                    User user =  datasnapshot.getValue(User.class);
                    for (String id : idlist){
                        if(user.getId().equals(id)){
                            mUser.add(user);
                        }

                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}