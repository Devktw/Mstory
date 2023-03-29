package com.mstudio.android.mstory.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLException;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.fullimage_activity;
import com.mstudio.android.mstory.app.activity.profile_activity;
import com.mstudio.android.mstory.app.activity.showall_post_activity;
import com.mstudio.android.mstory.app.fragment.home_recommend;
import com.mstudio.android.mstory.app.model.Post;
import com.mstudio.android.mstory.app.model.User;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class adapter_post extends RecyclerView.Adapter<adapter_post.ViewHolder> {
    Context mContext;
    List<Post> listpost ;
    private static adapter_post instance;
    private FirebaseUser firebaseUser;
    int gobalposition = 0;
    public adapter_post(Context context, List<Post> listpost) {
        this.mContext = context;
        this.listpost = listpost;
        instance=this;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_post, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = listpost.get(position);

        Glide.with(mContext.getApplicationContext()).load(post.getImage_post()).into(holder.image_post);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    Glide.with(mContext.getApplicationContext()).load(user.getImage_url()).into(holder.image_account_commentpost);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            if(post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                holder.layout_commenttpost.setVisibility(View.GONE);
                holder.layout_info.setVisibility(View.GONE);
            }else {
                holder.layout_commenttpost.setVisibility(View.VISIBLE);
                DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
                rf.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.child(post.getPublisher()).exists()){
                            holder.layout_info.setVisibility(View.GONE);
                        }else {
                            holder.layout_info.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
            }
        holder.date_time.setText(post.getDate_time());
        if (TextUtils.isEmpty(post.getCap_post())){
            holder.cap_post.setVisibility(View.GONE);
        }else {
            holder.cap_post.setVisibility(View.VISIBLE);
            holder.cap_post.setText(post.getCap_post());
            setTags(holder.cap_post,post.getCap_post());
        }
        if (post.getImage_post()==null){
            holder.layout_image_post.setVisibility(View.GONE);
            holder.cap_post.setTextSize(27);
            holder.layout_image_post.setVisibility(View.GONE);
        }else {
            holder.cap_post.setTextSize(20);
            holder.layout_image_post.setVisibility(View.VISIBLE);
        }
        publishInfo(holder.image_account,holder.username,holder.verified,post.getPublisher());
        like(post.getId(),holder.like,holder.value_like);
        urlike(holder.value_like,post.getId());

        view_post(post.getId(),holder.value_like);
        ur_view_post(holder.value_view,post.getId());
        ur_comment(holder.value_comment,post.getId());
        holder.value_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getId()).child(firebaseUser.getUid()).setValue(true);
                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                    holder.value_like.startAnimation(animation);
                    holder.value_like.setTextColor(mContext.getResources().getColor(R.color.like));
                }else {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getId()).child(firebaseUser.getUid()).removeValue();
                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                    holder.value_like.startAnimation(animation);
                    holder.value_like.setTextColor(mContext.getResources().getColor(R.color.tint_post));
                }
            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getId()).child(firebaseUser.getUid()).setValue(true);
                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                    holder.value_like.startAnimation(animation);
                    holder.value_like.setTextColor(mContext.getResources().getColor(R.color.like));
                }else {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getId()).child(firebaseUser.getUid()).removeValue();
                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                    holder.value_like.startAnimation(animation);
                    holder.value_like.setTextColor(mContext.getResources().getColor(R.color.tint_post));
                }
            }
        });


        holder.menu_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mySharedPreferences = mContext.getSharedPreferences("profile_activity", Context.MODE_PRIVATE);
                String checkactivity = mySharedPreferences.getString("profile_activity", "");
                if(checkactivity.equals("0")){
                    home_recommend.getInstance().showmenu_post(post.getId(),post.getPublisher(),position);
                }else {
                    profile_activity.getInstance().showmenu_post(post.getId(),post.getPublisher(),position);
                }

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mContext, showall_post_activity.class);
                i.putExtra("post_id", post.getId());
                i.putExtra("publisher", post.getPublisher());
                i.putExtra("image_post", post.getImage_post());
                i.putExtra("position", position);
                SharedPreferences mySharedPreferences = mContext.getSharedPreferences("deletepost", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putInt("position", position);
                editor.putString("post_id", post.getId());
                editor.commit();

                mContext.startActivity(i);
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, profile_activity.class);
                i.putExtra("publisher", post.getPublisher());
                mContext.startActivity(i);
            }
        });
        holder.image_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, profile_activity.class);
                i.putExtra("publisher", post.getPublisher());
                mContext.startActivity(i);
            }
        });
        holder.gocomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, showall_post_activity.class);
                i.putExtra("post_id", post.getId());
                i.putExtra("publisher", post.getPublisher());
                i.putExtra("gocomment", "1");
                mContext.startActivity(i);
            }
        });
        holder.gocomment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, showall_post_activity.class);
                i.putExtra("post_id", post.getId());
                i.putExtra("publisher", post.getPublisher());
                mContext.startActivity(i);
            }
        });
        holder.viewpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mySharedPreferences = mContext.getSharedPreferences("profile_activity", Context.MODE_PRIVATE);
                String checkactivity = mySharedPreferences.getString("profile_activity", "");
                if(checkactivity.equals("0")){
                    home_recommend.getInstance().showmenu_view_post(post.getId(),post.getPublisher());
                }else {
                    profile_activity.getInstance().showmenu_view_post(post.getId(),post.getPublisher());
                }
            }
        });
        holder.image_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, fullimage_activity.class);
                Pair<View, String> pair_image = Pair.create(holder.itemView.findViewById(R.id.layout_image_post),"image_post");
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(((Activity)mContext),(View)holder.image_post,"image_post");
                i.putExtra("post_id", post.getId());
                i.putExtra("publisher", post.getPublisher());
                mContext.startActivity(i,optionsCompat.toBundle());
            }
        });
    }
    private void setTags(TextView pTextView, String pTagString) {
        SpannableString string = new SpannableString(pTagString);

        int start = -1;
        for (int i = 0; i < pTagString.length(); i++) {
            if (pTagString.charAt(i) == '#') {
                start = i;
            } else if (pTagString.charAt(i) == ' ' || pTagString.charAt(i) == '\n' || (i == pTagString.length() - 1 && start != -1)) {
                if (start != -1) {
                    if (i == pTagString.length() - 1) {
                        i++; // case for if hash is last word and there is no
                        // space after word
                    }

                    final String tag = pTagString.substring(start, i);
                    string.setSpan(new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {
                            Log.d("Hash", String.format("Clicked %s!", tag));
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            // link color
                            ds.setColor(Color.parseColor("#3399FF"));
                            ds.setUnderlineText(false);
                        }
                    }, start, i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = -1;
                }
            }
        }

        pTextView.setMovementMethod(LinkMovementMethod.getInstance());
        pTextView.setText(string);
    }
    public static String formatNumber(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c", count / Math.pow(1000, exp),"kmgtpe".charAt(exp-1));
    }
    @Override
    public int getItemCount() {
        return (null != listpost ? listpost.size() : 0);
    }
    public static adapter_post getInstance() {
        return instance;
    }
    public void redata() {

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView verified;
        ImageView image_post;
        ImageView image_account;
        ImageView image_account_commentpost;
        TextView cap_post;
        TextView date_time;
        TextView username;
        CardView layout_image_post;
        CardView menu_post;
        ImageView like;
        TextView value_like;
        TextView value_comment;
        TextView value_send;
        TextView value_view;
        TextView gocomment;
        LinearLayout layout_commenttpost;
        LinearLayout layout_info;
        Context mContext;
        CardView gocomment2;
        CardView viewpost;
        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            like = itemView.findViewById(R.id.like);
            verified = itemView.findViewById(R.id.verified);
            gocomment = itemView.findViewById(R.id.gocomment);
            value_like = itemView.findViewById(R.id.value_like);
            value_comment = itemView.findViewById(R.id.value_comment);
            value_send = itemView.findViewById(R.id.value_send);
            value_view = itemView.findViewById(R.id.value_view);
            menu_post = itemView.findViewById(R.id.menu_post);
            date_time = itemView.findViewById(R.id.date_and_time);
            layout_image_post = itemView.findViewById(R.id.layout_image_post);
            image_account = itemView.findViewById(R.id.image_account);
            image_account_commentpost = itemView.findViewById(R.id.image_account_commentpost);
            username = itemView.findViewById(R.id.username);
            image_post = itemView.findViewById(R.id.image_post);
            cap_post = itemView.findViewById(R.id.tv_cap_post);
            layout_commenttpost = itemView.findViewById(R.id.layout_image_commentpost);
            gocomment2 = itemView.findViewById(R.id.gocomment2);
            layout_info = itemView.findViewById(R.id.layout_info);
            viewpost = itemView.findViewById(R.id.viewpost);
        }

    }
    private void like(String post_id,ImageView like,TextView like_tv){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("likes").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    like.setImageResource(R.drawable.ic_like_true);
                    like.setColorFilter(ContextCompat.getColor(mContext, R.color.like));
                    like.setTag("liked");
                    like_tv.setTextColor(mContext.getResources().getColor(R.color.like));
                }else {
                    like.setImageResource(R.drawable.ic_like_false);
                    like.setColorFilter(ContextCompat.getColor(mContext, R.color.tint_post));
                    like.setTag("like");
                    like_tv.setTextColor(mContext.getResources().getColor(R.color.tint_post));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void urlike(TextView value_like,String post_id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("likes").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                value_like.startAnimation(animation);
                value_like.setText(snapshot.getChildrenCount()+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void view_post(String post_id, TextView view_post_tv){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("viewpost").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){

                }else {

                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }
    private void ur_view_post(TextView value_view,String post_id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("viewpost").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                value_view.startAnimation(animation);
                value_view.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

    }



    private void ur_comment(TextView value_Comment,String post_id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id).child("Comment");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                value_Comment.startAnimation(animation);
                value_Comment.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

    }



    private void publishInfo(ImageView image_account,TextView username,ImageView verified,String user_id){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                FirebaseUser userData = FirebaseAuth.getInstance().getCurrentUser();
                Glide.with(mContext).load(user.getImage_url()).into(image_account);
                username.setText(user.getUser_name());

                if(snapshot.child("verified").exists()){
                    verified.setVisibility(View.VISIBLE);
                }else {
                    verified.setVisibility(View.GONE);
                }
                SharedPreferences mySharedPreferences = mContext.getSharedPreferences("position_post", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("position_post", String.valueOf(gobalposition));
                editor.commit();
            }
            //}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void addviewpost(){
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("postid", Context.MODE_PRIVATE);
        String postid = mySharedPreferences.getString("postid", "");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("viewpost").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){

                }else {
                    FirebaseDatabase.getInstance().getReference().child("viewpost").child(postid).child(firebaseUser.getUid()).setValue(true);

                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });


    }
}
