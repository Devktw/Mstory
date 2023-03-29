package com.mstudio.android.mstory.app.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.profile_activity;
import com.mstudio.android.mstory.app.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapter_searchuser extends RecyclerView.Adapter<adapter_searchuser.ViewHolde> {
    Context mContext;
    private List<User> mUser;
    private FirebaseUser firebaseuser;
    public adapter_searchuser(Context context,List<User> mUser) {
        this.mUser = mUser;
        this.mContext = context;
    }

    @Override
    public adapter_searchuser.ViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_searchuser, parent, false);

        return new ViewHolde(itemView);
    }

    @Override
    public void onBindViewHolder(adapter_searchuser.ViewHolde holder, int position) {
        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUser.get(position);
        holder.username.setText(user.getUser_name());
        holder.id.setText(user.getId());

        Glide.with(mContext.getApplicationContext()).load(user.getImage_url()).into(holder.image_account);

       if(user.getId().equals(firebaseuser.getUid())){
            holder.follow.setVisibility(View.GONE);
            holder.following.setVisibility(View.GONE);
       }else {
           isfollowing(user.getId(),holder.follow,holder.following);
       }


      verified(user.getId(),holder.verified);
        holder.follow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(holder.follow.getVisibility()==View.VISIBLE){
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseuser.getUid()).child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId()).child("follower").child(firebaseuser.getUid()).setValue(true);
                }

            }
        });
         holder.following.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(holder.following.getVisibility()==View.VISIBLE) {
                    FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseuser.getUid()).child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getId()).child("follower").child(firebaseuser.getUid()).removeValue();
                }
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, profile_activity.class);
                i.putExtra("publisher", user.getId());
                mContext.startActivity(i);
            }
        });
        holder.image_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, profile_activity.class);
                i.putExtra("publisher", user.getId());
                mContext.startActivity(i);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, profile_activity.class);
                i.putExtra("publisher", user.getId());
                mContext.startActivity(i);

            }
        });
    }
    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolde extends RecyclerView.ViewHolder {
        CircleImageView image_account;
        TextView username;
        TextView id;
        CardView follow;
        CardView following;
        ImageView verified;
        public ViewHolde(View itemView) {
            super(itemView);
            verified = itemView.findViewById(R.id.verified);
            image_account = itemView.findViewById(R.id.image_account);
            username = itemView.findViewById(R.id.username);
            id = itemView.findViewById(R.id.id_account);
            follow = itemView.findViewById(R.id.btn_follow);
            following = itemView.findViewById(R.id.btn_following);
        }
    }
    private void verified(String id, ImageView verified){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
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
    private void  isfollowing(String userid, CardView btn_follow,CardView btn_following){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("follow").child(firebaseuser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.child(userid).exists()){
                    btn_following.setVisibility(View.VISIBLE);
                    btn_follow.setVisibility(View.GONE);
                }else {
                    btn_following.setVisibility(View.GONE);
                    btn_follow.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
}