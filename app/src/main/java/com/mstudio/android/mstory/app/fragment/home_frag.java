package com.mstudio.android.mstory.app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.post_activity;


public class home_frag extends Fragment {
    FloatingActionButton go_postactivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.home_frag, container, false);
        go_postactivity=view.findViewById(R.id.go_postactivity);
        go_postactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), post_activity.class);
                startActivity(i);
            }
        });
        return view;
    }
}