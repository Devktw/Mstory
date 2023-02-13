package com.mstudio.android.mstory.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.mstudio.android.mstory.app.fragment.account_frag;
import com.mstudio.android.mstory.app.fragment.content_account_frag;
import com.mstudio.android.mstory.app.fragment.home_frag;
import com.mstudio.android.mstory.app.fragment.noti_frag;
import com.mstudio.android.mstory.app.fragment.search_frag;

import org.jetbrains.annotations.NotNull;

public class main extends AppCompatActivity {
    final Fragment fragment1 = new home_frag();
    final Fragment fragment2 = new noti_frag();
    final Fragment fragment3 = new search_frag();
    final Fragment fragment4 = new content_account_frag();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    FrameLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.main);
        content=findViewById(R.id.content);
        fm.beginTransaction().add(R.id.content, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.content, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.content, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.content,fragment1, "1").commit();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        fm.beginTransaction().hide(active).show(fragment1).commit();
                        active = fragment1;
                        break;
                    case R.id.search:
                        fm.beginTransaction().hide(active).show(fragment2).commit();
                        active = fragment2;
                        break;
                    case R.id.notification:
                        fm.beginTransaction().hide(active).show(fragment3).commit();
                        active = fragment3;
                        break;
                    case R.id.acount:
                        fm.beginTransaction().hide(active).show(fragment4).commit();
                        active = fragment4;
                        break;
                }
                return true;
            }
        });
    }
}