package com.mstudio.android.mstory.app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.post_activity;
import com.mstudio.android.mstory.app.main;
import com.mstudio.android.mstory.app.model.User;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class home_frag extends Fragment {

    ImageView image_account;
    TabLayout tabLayout;
    ViewPager viewPager;
    public FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTheme(R.style.AppTheme);
        final View view = inflater.inflate(R.layout.home_frag, container, false);
        image_account=view.findViewById(R.id.image_account);
        tabLayout=view.findViewById(R.id.tablayout);
        viewPager = view.findViewById(R.id.viewpager);
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getActivity()).load(user.getImage_url()).into(image_account);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseUser iscurrentUser = mAuth.getCurrentUser();
        if (iscurrentUser != null) {

        }

        setUpViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
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
        return view;
    }
    private void setUpViewPager(ViewPager viewPager) {

        home_frag.SectionPagerAdapter adapter = new home_frag.SectionPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new home_recommend(), "สำหรับเธอ");
        adapter.addFragment(new home_follow(), "กำลังติดตาม");
        adapter.addFragment(new like_frag(), "กำลังใช้งาน");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

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

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            titleList.add(title);
        }
    }

}