package com.mstudio.android.mstory.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Rect;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.LoginActivity;
import com.mstudio.android.mstory.app.activity.post_activity;
import com.mstudio.android.mstory.app.fragment.account_frag;
import com.mstudio.android.mstory.app.fragment.like_frag;
import com.mstudio.android.mstory.app.fragment.post_frag;
import com.mstudio.android.mstory.app.model.User;

import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapter_contentpost extends RecyclerView.Adapter<adapter_contentpost.CustomViewHolder> {
    private Context mContext;
    ImageView image_account;
    ImageView image_post;
    CardView close_image;
    TextView username;
    EditText editext_post;
    ImageView verified;
    private AdapterView.OnItemClickListener onItemClickListener;
    private static adapter_contentpost instance;
    public adapter_contentpost(Context context) {
        this.mContext = context;
        instance=this;


    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_post, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        image_account = view.findViewById(R.id.image_account);
        editext_post = view.findViewById(R.id.edittext_post);
        username = view.findViewById(R.id.username);
        close_image=view.findViewById(R.id.close_image);
        image_post=view.findViewById(R.id.image_post);
        verified = view.findViewById(R.id.verified);
        FirebaseUser userData = FirebaseAuth.getInstance().getCurrentUser();
        if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    username.setText(user.getUser_name());
                    Glide.with(mContext.getApplicationContext()).load(user.getImage_url()).into(image_account);
                    if(snapshot.child("verified").exists()){
                        verified.setVisibility(View.VISIBLE);
                    }else {
                        verified.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }
        editext_post.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.equals("")){
                    SharedPreferences sh = mContext.getSharedPreferences("editext_post", Context.MODE_PRIVATE);
                    sh.edit().remove("editext_post").commit();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_post.setVisibility(View.GONE);
                close_image.setVisibility(View.GONE);
                SharedPreferences settings = mContext.getSharedPreferences("image_post", Context.MODE_PRIVATE);
                settings.edit().remove("image_post").commit();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int position) {

    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return 1;
    }
    public static adapter_contentpost getInstance() {
        return instance;
    }
    public void setimaget() {
        image_post.setVisibility(View.VISIBLE);
        close_image.setVisibility(View.VISIBLE);
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("image_post", Context.MODE_PRIVATE);
        String data = mySharedPreferences.getString("image_post", "");
        Uri imageUri = Uri.parse(data);
        image_post.setImageURI(imageUri);
        post_activity.getInstance().hidebottom();

    }
    public void getstring() {
        String p = editext_post.getText().toString().trim();
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("edittext_post", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("edittext_post",p.toString());
        editor.commit();
    }
   
    static class CustomViewHolder extends RecyclerView.ViewHolder {

        public CustomViewHolder(View item) {
            super(item);
        }
    }
}