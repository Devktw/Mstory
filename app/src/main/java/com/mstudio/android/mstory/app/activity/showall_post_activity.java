package com.mstudio.android.mstory.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mstudio.android.mstory.app.LinearLayoutManagerWithSmoothScroller;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.adapter.adapter_multitype_allpost;
import com.mstudio.android.mstory.app.fragment.home_recommend;
import com.mstudio.android.mstory.app.model.User;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class showall_post_activity extends AppCompatActivity {
    TextView tv_username;
    RecyclerView recy_showall_post;
    adapter_multitype_allpost adapter;
    CardView exit;
    String string_usetrname;
    private static showall_post_activity instance;
    EditText edittext_comment;
    CircleImageView image_account;
    TextView tv_send_comment;
    LinearLayout layoutedittyext;
    RelativeLayout layout_comment_option;
    CircleImageView image_account_commentpost;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    boolean isedittextnull = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.content_showall_post_activity);
        instance = this;
        SharedPreferences mySharedPreferences = showall_post_activity.this.getSharedPreferences("delete", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("delete","1");
        editor.commit();
        String post_id = getIntent().getStringExtra("post_id");

        String publisher = getIntent().getStringExtra("publisher");

        String gocomment = getIntent().getStringExtra("gocomment");

        mDatabase = FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference().child("Posts").child(post_id).child("Comment");
        mStorage=FirebaseStorage.getInstance();

        layout_comment_option = findViewById(R.id.layout_comment_option);
        image_account_commentpost = findViewById(R.id.image_account_commentpost);
        tv_send_comment = findViewById(R.id.tv_send_comment);
        exit = findViewById(R.id.exit);
        tv_username = findViewById(R.id.tv_username);
        layoutedittyext = findViewById(R.id.layoutedittext);
        recy_showall_post = findViewById(R.id.recy_showall_post);
        recy_showall_post.setHasFixedSize(true);
        adapter = new adapter_multitype_allpost(this);
        LinearLayoutManagerWithSmoothScroller myLayoutManager = new LinearLayoutManagerWithSmoothScroller(this);
        recy_showall_post.setLayoutManager(myLayoutManager);
        recy_showall_post.setAdapter(adapter);
        string_usetrname =getIntent().getStringExtra("username");
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        image_account = findViewById(R.id.image_account_commentpost);





        DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("Users").child(publisher);
        rf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                tv_username.setText(user.getUser_name());
                    Glide.with(getApplicationContext()).load(user.getImage_url()).into(image_account);

            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        DatabaseReference rff = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        rff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImage_url()).into(image_account_commentpost);
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });



        edittext_comment = findViewById(R.id.edittext_comment);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) edittext_comment.getLayoutParams();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = 130;
        recy_showall_post.setPadding(0,0,0,150);
        tv_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = edittext_comment.getText().toString();
                if(!TextUtils.isEmpty(s)){
                    String commenttid = mRef.push().getKey();
                    String publisher = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                    String date_time = date + " • " + time;
                    HashMap<String,Object> hashmap = new HashMap<>();
                    hashmap.put("id",commenttid);
                    hashmap.put("comment",s);
                    hashmap.put("image_comment","");
                    hashmap.put("publisher",publisher);
                    hashmap.put("date_time",date_time);
                    mRef.child(commenttid).setValue(hashmap);
                    edittext_comment.clearFocus();
                    edittext_comment.getText().clear();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            showtoast("โพสต์ความคิดเห็นแล้ว");
                        }
                    }, 700);
                }

            }
        });
        if(!TextUtils.isEmpty(gocomment)){
            isedittextnull=false;
            edittext_comment.requestFocus();
            showkeyboard();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    recy_showall_post.smoothScrollToPosition(1);
                }
            }, 500);

        }
        edittext_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(TextUtils.isEmpty(charSequence)){
                    edittext_comment.getText().clear();
                    isedittextnull = true;
                    tv_send_comment.setTextColor(getResources().getColor(R.color.tint_post));
                }else {
                    isedittextnull=false;
                    tv_send_comment.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                if(edittext_comment.getLineCount()>1){
                    layoutedittyext.setBackgroundResource(R.drawable.item_edittext2);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) edittext_comment.getLayoutParams();
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    edittext_comment.setLayoutParams(params);
                }else {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) edittext_comment.getLayoutParams();
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    params.height = 130;
                    edittext_comment.setLayoutParams(params);
                    layoutedittyext.setBackgroundResource(R.drawable.item_edittext);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        KeyboardVisibilityEvent.setEventListener(showall_post_activity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen){
                    layout_comment_option.setVisibility(View.VISIBLE);
                    recy_showall_post.setPadding(0,0,0,280);
                }else {
                    if(edittext_comment.getText().toString().trim().length()==0){
                        layout_comment_option.setVisibility(View.GONE);
                    }
                    recy_showall_post.setPadding(0,0,0,150);
                }
            }
        });

    }
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
    public static showall_post_activity getInstance() {
        return instance;
    }
    public void showmenu_post(String post_id,String publisher) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_post, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(showall_post_activity.this, R.style.SheetDialog);
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
                finish();
                SharedPreferences mySharedPreferences = showall_post_activity.this.getSharedPreferences("delete", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("delete","1");
                editor.commit();


                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        home_recommend.getInstance().showtoast("ลบโพสต์ของคุณแล้ว");
                        home_recommend.getInstance().deletepost();
                    }
                }, 500);


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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(showall_post_activity.this, R.style.SheetDialog);
        TextView tv_value_viewpost;
        tv_value_viewpost = bottomSheetView.findViewById(R.id.tv_valueview_post);
        bottomSheetDialog.setContentView(bottomSheetView);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("viewpost").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Animation animation = AnimationUtils.loadAnimation(showall_post_activity.this, R.anim.slide_in_bottom);
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

    public void showkeyboard(){
       ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
               .toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    public void hidekeyboard(){
        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        recy_showall_post.setPadding(0,0,0,150);
    }

    @Override
    protected void onStop() {
        super.onStop();
        recy_showall_post.setPadding(0,0,0,0);
        SharedPreferences mySharedPreferences = showall_post_activity.this.getSharedPreferences("delete", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("delete","2");
        editor.commit();
    }
    public void showtoast(String s) {
        final Toast toast = new Toast(showall_post_activity.this.getApplicationContext());


        LayoutInflater inflater = showall_post_activity.this.getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast,
                (
                        ViewGroup) showall_post_activity.this.findViewById(R.id.customtoast));


        TextView tv = (TextView) layout.findViewById(R.id.tv_toast);
        tv.setText(s);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }
}
