package com.mstudio.android.mstory.app.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.adapter.adapter_post;
import com.mstudio.android.mstory.app.model.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class home_follow extends Fragment {
    private static home_follow instance;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    com.mstudio.android.mstory.app.adapter.adapter_post adapter_post;
    List<Post> listpost;
    List<String>  followLists;
    RecyclerView recycle_post;
    boolean isloadfirst;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int overallXScroll = 0;
    private int mCurrentIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_follow, container, false);
        instance=this;
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recycle_post=view.findViewById(R.id.recy_recommend);
        recycle_post.setHasFixedSize(true);
        recycle_post.setItemViewCacheSize(20);
        recycle_post.setDrawingCacheEnabled(true);
        recycle_post.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        listpost = new ArrayList<Post>();
        adapter_post = new adapter_post(getActivity(),listpost);

        recycle_post.setAdapter(adapter_post);
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(getActivity());
        myLayoutManager.setReverseLayout(true);
        myLayoutManager.setStackFromEnd(true);
        recycle_post.setLayoutManager(myLayoutManager);
        mSwipeRefreshLayout.setRefreshing(true);
        if(mSwipeRefreshLayout.isRefreshing()){
            checkfollowing();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 1000);
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                checkfollowing();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        recycle_post.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                overallXScroll = overallXScroll + dx;
                Log.i("check", "overall X  = " + overallXScroll);

                LinearLayoutManager lManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!recyclerView.canScrollVertically(1)) {

                    int position = getCurrentItem()+1;
                    if(position!=mCurrentIndex){
                        String post_id = listpost.get(position).getId();
                        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("postid", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("postid",post_id);
                        editor.commit();
                        adapter_post.addviewpost();
                        mCurrentIndex = position;
                    }

                }else{
                    int position = getCurrentItem();
                    if(position!=mCurrentIndex){
                        String post_id = listpost.get(position).getId();
                        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("postid", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("postid",post_id);
                        editor.commit();
                        adapter_post.addviewpost();
                        mCurrentIndex = position;
                    }

                }




            }
        });

        return view;
    }
    private int getCurrentItem(){
        LinearLayoutManager lManager = (LinearLayoutManager) recycle_post.getLayoutManager();
        int firstElementPosition = lManager.findLastVisibleItemPosition();
        return firstElementPosition;
    }

    private void checkfollowing(){
        followLists = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                followLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    followLists.add(dataSnapshot.getKey());
                }
                showpostFollow();

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void showpostFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                listpost.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id : followLists) {
                        if (post.getPublisher().equals(id)) {
                            listpost.add(post);
                            Collections.shuffle(listpost);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    recycle_post.smoothScrollToPosition(listpost.size());
                                }
                            }, 300);
                        }

                    }
                }

                adapter_post.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }
    public static home_follow getInstance() {
        return instance;
    }
    public void showmenu_post() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_post, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(),R.style.SheetDialog);
        LinearLayout delete_post = bottomSheetView.findViewById(R.id.delete_post);
        delete_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    int pagePosition = ((LinearLayoutManager) recycle_post.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                //listpost.remove(getPosition);
                adapter_post.notifyDataSetChanged();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // do something
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do something
            }
        });
        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // do something
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // do something
            }
        };
        bottomSheetDialog.show();
    }
}
