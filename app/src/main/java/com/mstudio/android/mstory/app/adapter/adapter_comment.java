package com.mstudio.android.mstory.app.adapter;

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
import com.mstudio.android.mstory.app.activity.profile_activity;
import com.mstudio.android.mstory.app.activity.showall_post_activity;
import com.mstudio.android.mstory.app.fragment.home_recommend;
import com.mstudio.android.mstory.app.model.Comment;
import com.mstudio.android.mstory.app.model.Post;
import com.mstudio.android.mstory.app.model.User;

import java.util.ArrayList;
import java.util.List;

public class adapter_comment extends RecyclerView.Adapter<adapter_comment.ViewHolder> {
    Context mContext;
    List<Comment> listcomment;
    private static adapter_comment instance;
    private FirebaseUser firebaseUser;
    public adapter_comment(Context context, List<Comment> listcomment) {
        this.mContext = context;
        this.listcomment = listcomment;
        instance = this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_comment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = listcomment.get(position);
        holder.comment.setText(comment.getComment());
        holder.date_time.setText(comment.getDate_time());
        publishInfo(holder.image_account,holder.username,holder.verified,comment.getPublisher());
        setTags(holder.comment,comment.getComment());

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, profile_activity.class);
                i.putExtra("publisher",comment.getPublisher());
                mContext.startActivity(i);
            }
        });
        holder.image_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, profile_activity.class);
                i.putExtra("publisher", comment.getPublisher());
                mContext.startActivity(i);
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
        return String.format("%.1f %c", count / Math.pow(1000, exp), "kmgtpe".charAt(exp - 1));
    }

    @Override
    public int getItemCount() {

        return (null != listcomment ? listcomment.size() : 0);
    }

    public static adapter_comment getInstance() {
        return instance;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_post;
        ImageView image_account;
        TextView comment;
        TextView date_time;
        TextView username;
        ImageView verified;

        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            verified = itemView.findViewById(R.id.verified);
            comment = itemView.findViewById(R.id.comment);
            date_time = itemView.findViewById(R.id.date_comment);
            image_account = itemView.findViewById(R.id.image_account_comment);
            username = itemView.findViewById(R.id.username);
           // image_post = itemView.findViewById(R.id.image_post);

        }
    }
}