package com.mstudio.android.mstory.app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.search_activity;
import com.mstudio.android.mstory.app.model.User;

import de.hdodenhof.circleimageview.CircleImageView;


public class search_frag extends Fragment {

    CardView go_search;
    CircleImageView image_account;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search_frag, container, false);
        image_account = view.findViewById(R.id.image_account);
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
        go_search = view.findViewById(R.id.go_search);

        go_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), search_activity.class);
                getActivity().startActivity(i);
            }
        });
        return view;
    }

}