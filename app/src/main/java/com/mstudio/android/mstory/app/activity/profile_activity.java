package com.mstudio.android.mstory.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.adapter.adapter_account;
import com.mstudio.android.mstory.app.adapter.adapter_profile;
import com.mstudio.android.mstory.app.fragment.home_recommend;
import com.mstudio.android.mstory.app.model.User;

public class profile_activity extends AppCompatActivity {
    RecyclerView recyclerView;
    adapter_profile adapter;
    private int overallXScroll = 0;
    TextView tv_username;
    CardView exit;
    private static profile_activity instance;
    CardView menu_account;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        instance = this;

        SharedPreferences mySharedPreferences =getSharedPreferences("profile_activity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("profile_activity","1");
        editor.commit();
        tv_username = findViewById(R.id.tv_username);
        exit = findViewById(R.id.exit);
        menu_account = findViewById(R.id.menu_account);
        String publisher = getIntent().getStringExtra("publisher");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisher);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                tv_username.setText(user.getUser_name());
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("Users");
        rf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(publisher)){
                    menu_account.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        recyclerView=findViewById(R.id.recy_profile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        adapter = new adapter_profile(this);
        recyclerView.setAdapter(adapter);

        menu_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showmenu_account(publisher);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                overallXScroll = overallXScroll + dx;
                Log.i("check","overall X  = " + overallXScroll);
                LinearLayoutManager myLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            }
        });
    }

    public static profile_activity getInstance() {
        return instance;
    }

    public void showmenu_post(String post_id,String publisher,int position) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_post, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
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
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
                reference.child(post_id).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        TextView tv_value_viewpost;
        tv_value_viewpost = bottomSheetView.findViewById(R.id.tv_valueview_post);
        bottomSheetDialog.setContentView(bottomSheetView);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("viewpost").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Animation animation = AnimationUtils.loadAnimation(profile_activity.this, R.anim.slide_in_bottom);
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



    public void showmenu_account(String publisher) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_post, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        LinearLayout block = bottomSheetView.findViewById(R.id.block);
        LinearLayout disble_notification = bottomSheetView.findViewById(R.id.disble_notification);

        LinearLayout follow = bottomSheetView.findViewById(R.id.follow);
        LinearLayout delete_post = bottomSheetView.findViewById(R.id.delete_post);
        LinearLayout edit_post = bottomSheetView.findViewById(R.id.edit_post);
        LinearLayout unfollow = bottomSheetView.findViewById(R.id.unfollow);
        LinearLayout noting_post = bottomSheetView.findViewById(R.id.notingpost);
        LinearLayout report_post = bottomSheetView.findViewById(R.id.report);
        follow.setVisibility(View.GONE);
        delete_post.setVisibility(View.GONE);
        edit_post.setVisibility(View.GONE);
        unfollow.setVisibility(View.GONE);
        noting_post.setVisibility(View.GONE);
        report_post.setVisibility(View.GONE);
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
            block.setVisibility(View.GONE);
            disble_notification.setVisibility(View.GONE);
        }else {

            DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
            rf.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(snapshot.child(publisher).exists()){
                        block.setVisibility(View.VISIBLE);
                        disble_notification.setVisibility(View.VISIBLE);
                    }else {
                        block.setVisibility(View.VISIBLE);
                        disble_notification.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
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

    @Override
    protected void onStop() {
        SharedPreferences mySharedPreferences =getSharedPreferences("profile_activity", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("profile_activity","0");
        editor.commit();
        super.onStop();
    }
}
