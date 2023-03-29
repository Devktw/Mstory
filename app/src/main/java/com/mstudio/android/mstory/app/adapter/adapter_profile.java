package com.mstudio.android.mstory.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.HeightWrappingViewPager;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.LoginActivity;
import com.mstudio.android.mstory.app.activity.details_profile_activity;
import com.mstudio.android.mstory.app.activity.editprofile_activity;
import com.mstudio.android.mstory.app.activity.fullimage_activity;
import com.mstudio.android.mstory.app.fragment.account_frag;
import com.mstudio.android.mstory.app.fragment.image_frag;
import com.mstudio.android.mstory.app.fragment.like_frag;
import com.mstudio.android.mstory.app.fragment.post_frag;
import com.mstudio.android.mstory.app.model.Post;
import com.mstudio.android.mstory.app.model.User;

import android.widget.RelativeLayout;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapter_profile extends RecyclerView.Adapter<adapter_profile.CustomViewHolder> {
    private Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    public adapter_profile(Context context) {

        this.mContext = context;


    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_frag, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
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

    class CustomViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profile;
        private TextView username;
        TabLayout tabLayout;
        HeightWrappingViewPager viewPager;
        ImageView image_profile;
        TextView value_post;
        TextView value_following;
        TextView value_follow;
        TextView bio;

        ImageView verified;
        LinearLayout post;
        LinearLayout following;
        LinearLayout follower;

        CardView btn_follow;
        CardView btn_following;
        CardView btn_editprofile;
        private int[] tabIcons = {
                R.drawable.ic_listprofile,
                R.drawable.ic_image,
                R.drawable.ic_like_false,
                R.drawable.ic_favorite_false,
        };
        boolean ismyid;

        public CustomViewHolder(View item) {
            super(item);
            mContext = item.getContext();

            value_post = item.findViewById(R.id.value_post);
            value_following = item.findViewById(R.id.value_following);
            value_follow = item.findViewById(R.id.value_follow);
            bio = item.findViewById(R.id.bio);

            verified = itemView.findViewById(R.id.verified);
            post = item.findViewById(R.id.post);
            following = item.findViewById(R.id.following);
            follower = item.findViewById(R.id.follower);


            btn_follow = item.findViewById(R.id.btn_follow);
            btn_following = item.findViewById(R.id.btn_following);
            btn_editprofile=item.findViewById(R.id.btn_editprofile);


            profile = item.findViewById(R.id.image_account);
            username = item.findViewById(R.id.username);
            tabLayout=item.findViewById(R.id.tablayout);
            viewPager=item.findViewById(R.id.viewpager);
            image_profile=item.findViewById(R.id.image_profile);
            String publisher = ((Activity)mContext).getIntent().getStringExtra("publisher");

            follower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, details_profile_activity.class);
                    i.putExtra("publisher", publisher);
                    i.putExtra("gofollower", "gofollower");
                    mContext.startActivity(i);
                }
            });
            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, details_profile_activity.class);
                    i.putExtra("publisher", publisher);
                    i.putExtra("gofollowing", "gofollowing");
                    mContext.startActivity(i);
                }
            });
            btn_editprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, editprofile_activity.class);
                    i.putExtra("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mContext.startActivity(i);
                }
            });
            setUpViewPager(viewPager);

            tabLayout.setupWithViewPager(viewPager);
            setupTabIcons();
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(final TabLayout.Tab tab) {
                    int position = tab.getPosition();

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }

            });



            image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // Intent i = new Intent(mContext, fullimage_activity.class);
                    //Pair<View, String> pair_image = Pair.create(itemView.findViewById(R.id.image_account),"image_post");
                   // ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(((Activity)mContext),(View)profile,"image_post");
                    //i.putExtra("post_id", post.getId());
                   // i.putExtra("publisher", post.getPublisher());
                    //mContext.startActivity(i,optionsCompat.toBundle());
                }
            });
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // Intent i = new Intent(mContext, fullimage_activity.class);
                    //Pair<View, String> pair_image = Pair.create(holder.itemView.findViewById(R.id.layout_image_post),"image_post");
                    //ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(((Activity)mContext),(View)holder.image_post,"image_post");
               // i.putExtra("post_id", post.getId());
                   // i.putExtra("publisher", post.getPublisher());
                   // mContext.startActivity(i,optionsCompat.toBundle());
                }
            });
            btn_follow.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    if(btn_follow.getVisibility()==View.VISIBLE){
                        FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(publisher).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("follow").child(publisher).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    }

                }
            });
           btn_following.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    if(btn_following.getVisibility()==View.VISIBLE) {
                        FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(publisher).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("follow").child(publisher).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    }
                }
            });
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisher);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    username.setText(user.getUser_name());

                    Glide.with((Activity)mContext).load(user.getImage_url()).into(profile);
                    Glide.with((Activity)mContext).load(user.getImage_profile()).into(image_profile);
                    bio.setText(user.getBio_account());

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

            DatabaseReference rfmy = FirebaseDatabase.getInstance().getReference().child("Users");
            rfmy.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(publisher)){
                        btn_editprofile.setVisibility(View.VISIBLE);
                        btn_following.setVisibility(View.GONE);
                        btn_follow.setVisibility(View.GONE);
                        ismyid = true;
                    }else {
                        btn_editprofile.setVisibility(View.GONE);
                        btn_following.setVisibility(View.GONE);
                        btn_follow.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });
            DatabaseReference rffff = FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
            rffff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(!ismyid){
                        if(snapshot.child(publisher).exists()){
                            btn_following.setVisibility(View.VISIBLE);
                            btn_follow.setVisibility(View.GONE);
                        }else {
                            btn_following.setVisibility(View.GONE);
                            btn_follow.setVisibility(View.VISIBLE);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });


            DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("follow").child(publisher).child("following");
            rf.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                    value_following.setText(snapshot.getChildrenCount()+"");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            DatabaseReference rff = FirebaseDatabase.getInstance().getReference().child("follow").child(publisher).child("follower");
            rff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                    value_follow.setText(snapshot.getChildrenCount()+"");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            DatabaseReference rfff = FirebaseDatabase.getInstance().getReference().child("Posts");
            rfff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    int postvalue = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                         Post post = dataSnapshot.getValue(Post.class);
                        if(post.getPublisher().equals(publisher)){
                            postvalue++;
                        }

                    }
                    value_post.setText(postvalue+"");
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        class SectionPagerAdapter extends FragmentPagerAdapter {

            private List<Fragment> fragmentList = new ArrayList<>();
            private List<String> titleList = new ArrayList<>();

            public SectionPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }

            public void addFragment(Fragment fragment, String title)    {
                fragmentList.add(fragment);
                titleList.add(title);
            }
        }
        private void setupTabIcons() {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
            tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        }


        private void setUpViewPager(ViewPager viewPager) {
            FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
            account_frag.SectionPagerAdapter adapter = new account_frag.SectionPagerAdapter(fm);

            adapter.addFragment(new post_frag(), "");
            adapter.addFragment(new image_frag(), "");
            adapter.addFragment(new like_frag(), "");
            adapter.addFragment(new like_frag(), "");
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(8);

        }

    }

}
