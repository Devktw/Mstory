package com.mstudio.android.mstory.app.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.LinearLayoutManagerWrapper;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.adapter.adapter_image_account;
import com.mstudio.android.mstory.app.adapter.adapter_post;
import com.mstudio.android.mstory.app.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class image_frag extends Fragment {
    RecyclerView recy_post;
    adapter_image_account adapter;
    List<Post> listpost;
    private int overallXScroll = 0;
    private int mCurrentIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.post_frag, container, false);
        recy_post = view.findViewById(R.id.recy_post);
        recy_post.setHasFixedSize(true);
        recy_post.setItemViewCacheSize(20);
        recy_post.setDrawingCacheEnabled(true);
        recy_post.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        listpost = new ArrayList<Post>();
        adapter = new adapter_image_account(getActivity(), listpost);

        recy_post.setAdapter(adapter);
        GridLayoutManager myLayoutManager = new GridLayoutManager(getActivity(), 3);
        recy_post.setLayoutManager(myLayoutManager);

        loadData();
        return view;
    }

    private int getCurrentItem() {
        LinearLayoutManager lManager = (LinearLayoutManager) recy_post.getLayoutManager();
        int firstElementPosition = lManager.findLastVisibleItemPosition();
        return firstElementPosition;
    }

    private void loadData() {
        String publisher = getActivity().getIntent().getStringExtra("publisher");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                listpost.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (TextUtils.isEmpty(publisher)) {
                        if (post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            if(!TextUtils.isEmpty(post.getImage_post())){
                                listpost.add(post);
                                Collections.reverse(listpost);
                            }

                        }
                    }else {
                        if (post.getPublisher().equals(publisher)) {
                            if(!TextUtils.isEmpty(post.getImage_post())){
                                listpost.add(post);
                                Collections.reverse(listpost);
                            }

                        }
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

