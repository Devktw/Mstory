package com.mstudio.android.mstory.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.Html;
import android.widget.ImageView;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.view.View.OnClickListener;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.mstudio.android.mstory.app.activity.fullimage_activity;
import com.mstudio.android.mstory.app.activity.profile_activity;
import com.mstudio.android.mstory.app.activity.showall_post_activity;
import com.mstudio.android.mstory.app.fragment.home_recommend;
import com.mstudio.android.mstory.app.model.Comment;
import com.mstudio.android.mstory.app.model.Post;
import com.mstudio.android.mstory.app.model.User;

public class adapter_multitype_allpost extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private static final int LAYOUT_ONE = 0;
    private static final int LAYOUT_TWO = 1;
    public adapter_multitype_allpost(Context context) {
        this.mContext = context;

    }
    @Override
    public int getItemViewType(int position)
    {
        switch (position) {
            case LAYOUT_ONE:

                return LAYOUT_ONE;
            case LAYOUT_TWO:

                return LAYOUT_TWO;
        }

        return 2;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        switch (viewType) {
            case LAYOUT_ONE:
                view = LayoutInflater.from(mContext).inflate(R.layout.showall_post, parent, false);
                final ViewHolderOne viewHolderone = new ViewHolderOne(view);
                return viewHolderone;
            case LAYOUT_TWO:
                mContext = parent.getContext();
                view = LayoutInflater.from(mContext).inflate(R.layout.content_comment, parent, false);
                ViewHolderTwo viewHoldertwo = new ViewHolderTwo(view);


                return viewHoldertwo;

        }


        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()) {
            case LAYOUT_ONE:
                final ViewHolderOne one = (ViewHolderOne) holder;
                break;
            case LAYOUT_TWO:
                ViewHolderTwo two = (ViewHolderTwo) holder;
                break;
        }



    }


    @Override
    public int getItemCount() {
        return 2;
    }

    public class ViewHolderOne extends RecyclerView.ViewHolder {
        private Context mContext;
        ImageView image_account;
        ImageView image_post;
        TextView username;
        TextView date_time;
        TextView cap_post;
        String string_tv_cappost,string_username,string_image_account,string_date_time,string_value_like,string_value_comment,string_value_send,string_value_view;
        TextView value_like;
        TextView value_comment;
        TextView value_view;
        TextView value_send;
        CardView menu;
        ImageView like;
        LinearLayout layout_info;
        CardView viewpost;
        boolean deletedata;
        ImageView verified;
        public ViewHolderOne(View view) {
            super(view);
            this.mContext = view.getContext();
            String post_id = ((Activity)mContext).getIntent().getStringExtra("post_id");
            String string_image_post = ((Activity)mContext).getIntent().getStringExtra("image_post");
            String publisher = ((Activity)mContext).getIntent().getStringExtra("publisher");
            String position_string = ((Activity)mContext).getIntent().getStringExtra("position");

            verified = itemView.findViewById(R.id.verified);
            like = view.findViewById(R.id.like);
            menu = view.findViewById(R.id.menu_post);
            image_post = view.findViewById(R.id.image_post);
            date_time = view.findViewById(R.id.date_and_time);
            cap_post = view.findViewById(R.id.tv_cap_post);
            value_like = view.findViewById(R.id.value_like);
            value_view = view.findViewById(R.id.value_view);
            value_comment = view.findViewById(R.id.value_comment);
            value_send = view.findViewById(R.id.value_send);
            image_account = view.findViewById(R.id.image_account);
            username = view.findViewById(R.id.username);
            image_post = view.findViewById(R.id.image_post);
            layout_info = itemView.findViewById(R.id.layout_info);
            viewpost = itemView.findViewById(R.id.viewpost);
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, profile_activity.class);
                    i.putExtra("publisher", publisher);
                    mContext.startActivity(i);
                }
            });
            image_account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, profile_activity.class);
                    i.putExtra("publisher", publisher);
                    mContext.startActivity(i);
                }
            });
            image_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, fullimage_activity.class);
                    Pair<View, String> pair_image = Pair.create(view.findViewById(R.id.layout_image_post),"image_post");
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(((Activity)mContext),(View)image_post,"image_post");
                    i.putExtra("post_id", post_id);
                    i.putExtra("publisher", publisher);
                    mContext.startActivity(i,optionsCompat.toBundle());
                }
            });
            if(publisher.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                layout_info.setVisibility(View.GONE);
            }else {
                DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
                rf.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.child(publisher).exists()){
                            layout_info.setVisibility(View.GONE);
                        }else {
                            layout_info.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

            }


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("delete", Context.MODE_PRIVATE);
                        String delete = mySharedPreferences.getString("delete", "");
                        Post post = snapshot.getValue(Post.class);
                        if(!delete.equals("2")){
                            Glide.with(mContext.getApplicationContext()).load(post.getImage_post()).into(image_post);
                            date_time.setText(post.getDate_time());
                            if (TextUtils.isEmpty(post.getCap_post())) {
                                cap_post.setVisibility(View.GONE);
                            } else {
                                cap_post.setVisibility(View.VISIBLE);
                                cap_post.setText(post.getCap_post());
                                setTags(cap_post, post.getCap_post());
                                if (TextUtils.isEmpty(post.getImage_post())) {
                                    cap_post.setTextSize(30);
                                }
                            }


                            DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("viewpost").child(post_id);
                            rf.addValueEventListener(new ValueEventListener() {
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
                    }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference rff = FirebaseDatabase.getInstance().getReference().child("Users").child(publisher);
            rff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    Glide.with(mContext.getApplicationContext()).load(user.getImage_url()).into(image_account);
                    username.setText(user.getUser_name());
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
            value_like.setText(string_value_like);
            like(post_id,like,value_like);
            urlike(value_like,post_id);
            value_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(like.getTag().equals("like")){
                        FirebaseDatabase.getInstance().getReference().child("likes").child(post_id).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                        value_like.startAnimation(animation);
                        value_like.setTextColor(mContext.getResources().getColor(R.color.like));
                    }else {
                        FirebaseDatabase.getInstance().getReference().child("likes").child(post_id).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                        value_like.startAnimation(animation);
                        value_like.setTextColor(mContext.getResources().getColor(R.color.tint_post));
                    }
                }
            });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(like.getTag().equals("like")){
                        FirebaseDatabase.getInstance().getReference().child("likes").child(post_id).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                        value_like.startAnimation(animation);
                        value_like.setTextColor(mContext.getResources().getColor(R.color.like));
                    }else {
                        FirebaseDatabase.getInstance().getReference().child("likes").child(post_id).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                        value_like.startAnimation(animation);
                        value_like.setTextColor(mContext.getResources().getColor(R.color.tint_post));
                    }
                }
            });
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id).child("Comment");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
                        value_comment.startAnimation(animation);
                        value_comment.setText(snapshot.getChildrenCount() + "");
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });

            viewpost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showall_post_activity.getInstance().showmenu_view_post(post_id,publisher);
                }
            });



            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                        showall_post_activity.getInstance().showmenu_post(post_id,publisher);

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


    //****************  VIEW HOLDER 2 ******************//

    public class ViewHolderTwo extends RecyclerView.ViewHolder {

        RecyclerView recy_comment;

        List<Comment> listcomment;
        adapter_comment adapter_comment;
        Context context;
        public ViewHolderTwo(View itemView) {
            super(itemView);
            context = itemView.getContext();

            recy_comment = (RecyclerView) itemView.findViewById(R.id.recy_comment);
            recy_comment.setHasFixedSize(true);
            recy_comment.setItemViewCacheSize(20);
            recy_comment.setDrawingCacheEnabled(true);
            recy_comment.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            listcomment = new ArrayList<Comment>();
            adapter_comment = new adapter_comment(context, listcomment);
            recy_comment.setAdapter(adapter_comment);
            LinearLayoutManager myLayoutManager = new LinearLayoutManager(context);
            recy_comment.setLayoutManager(myLayoutManager);
            String post_id = ((Activity)mContext).getIntent().getStringExtra("post_id");
            String publisher = ((Activity)mContext).getIntent().getStringExtra("publisher");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id).child("Comment");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    listcomment.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Comment comment = dataSnapshot.getValue(Comment.class);
                        Collections.reverse(listcomment);
                        listcomment.add(comment);
                    }
                    adapter_comment.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }

}

