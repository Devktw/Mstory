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

public class following_fag extends Fragment {
    private static following_fag instance;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    RecyclerView recy_folowing;
    adapter_searchuser adapter;
    List<User> mUser;
    boolean isloadfirst;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int overallXScroll = 0;
    private int mCurrentIndex = 0;
    List<String> idlist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.following_frag, container, false);
        instance=this;
        String publisher = getActivity().getIntent().getStringExtra("publisher");
        recy_folowing=view.findViewById(R.id.recy_following);
        recy_folowing.setHasFixedSize(true);
        recy_folowing.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUser = new ArrayList<>();
        adapter = new adapter_searchuser(getActivity(),mUser);
        recy_folowing.setAdapter(adapter);
        idlist = new ArrayList<>();
        getFollowing();

        return view;
    }
    private void getFollowing(){
        String publisher = getActivity().getIntent().getStringExtra("publisher");
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("follow").child(publisher).child("following");
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
