package com.mstudio.android.mstory.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.HeightWrappingViewPager;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.fragment.account_frag;
import com.mstudio.android.mstory.app.fragment.follower_fag;
import com.mstudio.android.mstory.app.fragment.following_fag;
import com.mstudio.android.mstory.app.fragment.image_frag;
import com.mstudio.android.mstory.app.fragment.like_frag;
import com.mstudio.android.mstory.app.fragment.post_frag;
import com.mstudio.android.mstory.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class details_profile_activity extends AppCompatActivity {
    private static details_profile_activity instance;
    TextView tv_username;
    CardView exit;
    TabLayout tabLayout;
    HeightWrappingViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.details_profile_activity);
        tabLayout=findViewById(R.id.tablayout);
        viewPager=findViewById(R.id.viewpager);
        tv_username = findViewById(R.id.tv_username);
        exit = findViewById(R.id.exit);
        String publisher = getIntent().getStringExtra("publisher");
        String gofollower = getIntent().getStringExtra("gofollower");
        String gofollowing = getIntent().getStringExtra("gofollowing");

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
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        instance = this;

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
        if(gofollower!=null){
            if(!gofollower.equals("")){
                tabLayout.selectTab(tabLayout.getTabAt(1));
            }
        }

        if(gofollowing!=null){
            if(!gofollowing.equals("")){
                tabLayout.selectTab(tabLayout.getTabAt(0));
            }
        }

    }
    public static details_profile_activity getInstance() {
        return instance;
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

        @org.jetbrains.annotations.Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        public void addFragment(Fragment fragment, String title)    {
            fragmentList.add(fragment);
            titleList.add(title);
        }
    }

    private void setUpViewPager(ViewPager viewPager) {
        String publisher = getIntent().getStringExtra("publisher");
        FragmentManager fm = getSupportFragmentManager();
        account_frag.SectionPagerAdapter adapter = new account_frag.SectionPagerAdapter(fm);
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("follow").child(publisher).child("following");
        rf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference rff = FirebaseDatabase.getInstance().getReference().child("follow").child(publisher).child("follower");
        rff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.addFragment(new following_fag(), "กำลังติดตาม");
        adapter.addFragment(new follower_fag(), "ผู้ติดตาม");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(8);

    }

}
