package com.mstudio.android.mstory.app.fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.LinearLayoutManagerWrapper;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.adapter.adapter_post;
import com.mstudio.android.mstory.app.model.Post;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class post_frag extends Fragment {
    RecyclerView recy_post;
    adapter_post adapter;
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
        adapter = new adapter_post(getActivity(), listpost);

        recy_post.setAdapter(adapter);
        LinearLayoutManagerWrapper myLayoutManager = new LinearLayoutManagerWrapper(getActivity(), LinearLayoutManagerWrapper.VERTICAL, false);
        recy_post.setLayoutManager(myLayoutManager);
        myLayoutManager.setReverseLayout(true);
        myLayoutManager.setStackFromEnd(true);
        recy_post.setNestedScrollingEnabled(false);
        recy_post.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                overallXScroll = overallXScroll + dx;
                Log.i("check", "overall X  = " + overallXScroll);
                if (!recyclerView.canScrollVertically(1)) {

                }
                LinearLayoutManager lManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!recyclerView.canScrollVertically(1)) {

                    int position = getCurrentItem();
                    if (position != mCurrentIndex) {
                        String post_id = listpost.get(position).getId();
                        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("postid", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("postid", post_id);
                        editor.commit();
                        adapter.addviewpost();
                        mCurrentIndex = position;
                    }

                } else {
                    int position = getCurrentItem();
                    if (position != mCurrentIndex) {
                        String post_id = listpost.get(position).getId();
                        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("postid", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("postid", post_id);
                        editor.commit();
                        adapter.addviewpost();
                        mCurrentIndex = position;
                    }

                }
            }
        });
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
                            listpost.add(post);
                        }
                    }else {
                        if (post.getPublisher().equals(publisher)) {
                            listpost.add(post);
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
