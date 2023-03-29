package com.mstudio.android.mstory.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.mstudio.android.mstory.app.model.Comment;
import com.mstudio.android.mstory.app.model.Post;
import com.mstudio.android.mstory.app.model.User;

import java.util.List;

public class adapter_image_account extends RecyclerView.Adapter<adapter_image_account.ViewHolder> {
    Context mContext;
    List<Post> listimage;
    private static adapter_image_account instance;
    private FirebaseUser firebaseUser;
    public adapter_image_account(Context context, List<Post> listimage) {
        this.mContext = context;
        this.listimage = listimage;
        instance = this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_image, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = listimage.get(position);
        Glide.with(mContext).load(post.getImage_post()).into(holder.image);
    }


    public static String formatNumber(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c", count / Math.pow(1000, exp), "kmgtpe".charAt(exp - 1));
    }

    @Override
    public int getItemCount() {

        return (null != listimage ? listimage.size() : 0);
    }

    public static adapter_image_account getInstance() {
        return instance;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;


        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            image = itemView.findViewById(R.id.image);

        }
    }
}