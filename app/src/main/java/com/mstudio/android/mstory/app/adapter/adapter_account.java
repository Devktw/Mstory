package com.mstudio.android.mstory.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.LoginActivity;
import com.mstudio.android.mstory.app.fragment.account_frag;
import com.mstudio.android.mstory.app.fragment.like_frag;
import com.mstudio.android.mstory.app.fragment.post_frag;

import android.widget.RelativeLayout;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapter_account extends RecyclerView.Adapter<adapter_account.CustomViewHolder> {
    private Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    public adapter_account(Context context) {

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
        ViewPager viewPager;
        ImageView image_profile;
        private int[] tabIcons = {
                R.drawable.ic_listprofile,
                R.drawable.ic_like,

        };
        public CustomViewHolder(View item) {
            super(item);
            mContext = item.getContext();
            profile = item.findViewById(R.id.image_account);
            username = item.findViewById(R.id.username);
            tabLayout=item.findViewById(R.id.tablayout);
            viewPager=item.findViewById(R.id.viewpager);
            image_profile=item.findViewById(R.id.image_profile);
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
            FirebaseUser userData = FirebaseAuth.getInstance().getCurrentUser();
            if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {
                username.setText(userData.getDisplayName());

                Glide.with((Activity)mContext).load(userData.getPhotoUrl().toString()).into(profile);
                Glide.with((Activity)mContext).load(userData.getPhotoUrl().toString()).into(image_profile);
            }
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

        }


        private void setUpViewPager(ViewPager viewPager) {
            FragmentManager fm = ((AppCompatActivity)mContext).getSupportFragmentManager();
            account_frag.SectionPagerAdapter adapter = new account_frag.SectionPagerAdapter(fm);

            adapter.addFragment(new post_frag(), "");
            adapter.addFragment(new like_frag(), "");
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(8);

        }
    }
}
