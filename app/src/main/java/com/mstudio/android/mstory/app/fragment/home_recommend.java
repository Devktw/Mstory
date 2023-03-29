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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mstudio.android.mstory.app.LinearLayoutManagerWrapper;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.post_activity;
import com.mstudio.android.mstory.app.adapter.adapter_post;
import com.mstudio.android.mstory.app.model.Post;
import com.mstudio.android.mstory.app.model.User;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class home_recommend extends Fragment {
    private static home_recommend instance;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    adapter_post adapter_post;
    List<Post> listpostmain;
    //List<Post> listmypost;
   // List<Post> listfollowpost;
    List<String> followLists;
    List<String> mypostLists;
    RecyclerView recycle_post;
    boolean isfrist = true;
    boolean isloadfirst;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int overallXScroll = 0;
    boolean refresh;
    boolean isdeletedata;
    CardView layout_dinamic;
    private int mCurrentIndex = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.home_recommend, container, false);
        instance = this;
        layout_dinamic = view.findViewById(R.id.layout_dinamic);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recycle_post = view.findViewById(R.id.recy_recommend);
        recycle_post.setHasFixedSize(true);
        recycle_post.setItemViewCacheSize(20);
        recycle_post.setDrawingCacheEnabled(true);
        recycle_post.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        listpostmain = new ArrayList<Post>();
       // listmypost = new ArrayList<Post>();
        //listfollowpost = new ArrayList<Post>();
        adapter_post = new adapter_post(getActivity(), listpostmain);

        recycle_post.setAdapter(adapter_post);
        LinearLayoutManagerWrapper myLayoutManager = new LinearLayoutManagerWrapper(getActivity(), LinearLayoutManagerWrapper.VERTICAL, false);
        recycle_post.setLayoutManager(myLayoutManager);
        myLayoutManager.setReverseLayout(true);
        myLayoutManager.setStackFromEnd(true);
        checkmyhpost();


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isfrist = true;
                refresh = true;
                mSwipeRefreshLayout.setRefreshing(true);
                checkfollowing();
                checkmyhpost();
            }
        });
        recycle_post.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if(position!=mCurrentIndex){
                        String post_id = listpostmain.get(position).getId();
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
                        String post_id = listpostmain.get(position).getId();
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



    private void checkmyhpost() {
        mypostLists = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                mypostLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    mypostLists.add(dataSnapshot.getKey());
                }
                showpostFollow();
                checkfollowing();

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void checkfollowing() {
        followLists = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                followLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    followLists.add(dataSnapshot.getKey());
                }
                showpostFollow();
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void showpostFollow() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                listpostmain.clear();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        listpostmain.add(post);
                    }
                    for(String id : followLists){
                        if(post.getPublisher().equals(id)){
                            listpostmain.add(post);
                        }
                    }
                }
                if(isfrist){
                    recycle_post.setVisibility(View.GONE);

                    isfrist = false;
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Collections.shuffle(listpostmain);
                            recycle_post.smoothScrollToPosition(listpostmain.size());
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    recycle_post.setVisibility(View.VISIBLE);
                                }
                            }, 500);
                        }
                    }, 500);
                }
                if(!isdeletedata){
                    if(isfrist){
                        adapter_post.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

    }


    public static home_recommend getInstance() {
        return instance;
    }

    public void deletepost(){

        SharedPreferences sh = getActivity().getSharedPreferences("deletepost", Context.MODE_PRIVATE);
        int position = sh.getInt("position", 0);
        String post_id = sh.getString("post_id", "");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.child(post_id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showtoast("ลบโพสต์ของคุณแล้ว");
                        adapter_post.notifyItemChanged(position);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                isdeletedata = false;

                            }
                        }, 500);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    public void showmenu_post(String post_id,String publisher,int position) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_post, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        LinearLayout follow = bottomSheetView.findViewById(R.id.follow);
        LinearLayout delete_post = bottomSheetView.findViewById(R.id.delete_post);
        LinearLayout edit_post = bottomSheetView.findViewById(R.id.edit_post);
        LinearLayout unfollow = bottomSheetView.findViewById(R.id.unfollow);
        LinearLayout block = bottomSheetView.findViewById(R.id.block);
        LinearLayout disble_notification = bottomSheetView.findViewById(R.id.disble_notification);
        LinearLayout noting_post = bottomSheetView.findViewById(R.id.notingpost);
        LinearLayout report_post = bottomSheetView.findViewById(R.id.report);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });


        if(publisher.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            edit_post.setVisibility(View.VISIBLE);
            delete_post.setVisibility(View.VISIBLE);
            follow.setVisibility(View.GONE);
            unfollow.setVisibility(View.GONE);
            block.setVisibility(View.GONE);
            noting_post.setVisibility(View.GONE);
            disble_notification.setVisibility(View.GONE);
            report_post.setVisibility(View.GONE);
        }else {
            edit_post.setVisibility(View.GONE);
            delete_post.setVisibility(View.GONE);
            DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
            rf.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.child(publisher).exists()){
                        follow.setVisibility(View.GONE);
                        unfollow.setVisibility(View.VISIBLE);
                        block.setVisibility(View.VISIBLE);
                        noting_post.setVisibility(View.VISIBLE);
                        disble_notification.setVisibility(View.VISIBLE);
                        report_post.setVisibility(View.VISIBLE);
                    }else {
                        unfollow.setVisibility(View.GONE);
                        follow.setVisibility(View.VISIBLE);
                        block.setVisibility(View.VISIBLE);
                        noting_post.setVisibility(View.GONE);
                        disble_notification.setVisibility(View.GONE);
                        report_post.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }




        delete_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isdeletedata = true;
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
                reference.child(post_id).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showtoast("ลบโพสต์ของคุณแล้ว");
                                adapter_post.notifyItemChanged(position);
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        isdeletedata = false;

                                    }
                                }, 500);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

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

    public void showmenu_view_post(String post_id,String publisher) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_viewpost, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        TextView tv_value_viewpost;
        tv_value_viewpost = bottomSheetView.findViewById(R.id.tv_valueview_post);
        bottomSheetDialog.setContentView(bottomSheetView);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("viewpost").child(post_id);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_bottom);
                    tv_value_viewpost.startAnimation(animation);
                    tv_value_viewpost.setText(snapshot.getChildrenCount()+"");
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });


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






    public void showtoast(String s) {
        final Toast toast = new Toast(getActivity().getApplicationContext());


        LayoutInflater inflater = getActivity().getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast,
                (
                        ViewGroup) getActivity().findViewById(R.id.customtoast));


        TextView tv = (TextView) layout.findViewById(R.id.tv_toast);
        tv.setText(s);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
        if(s.equals("แชร์โพสต์ของคุณแล้ว")){
            adapter_post.notifyItemInserted(listpostmain.size());
        }
    }
}
