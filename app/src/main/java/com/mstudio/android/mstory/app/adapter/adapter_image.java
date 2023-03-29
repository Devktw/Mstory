package com.mstudio.android.mstory.app.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.editprofile_activity;
import com.mstudio.android.mstory.app.activity.post_activity;

import java.util.ArrayList;
import java.util.List;

public class adapter_image extends RecyclerView.Adapter<adapter_image.MyViewHolde> {
    Context context;
    private ArrayList<String> imageData = new ArrayList<String>();

    public adapter_image(ArrayList<String> imageData, FragmentActivity activity) {
        this.imageData = imageData;
        this.context = activity;
    }

    @Override
    public adapter_image.MyViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_image, parent, false);

        return new MyViewHolde(itemView);
    }

    @Override
    public void onBindViewHolder(adapter_image.MyViewHolde holder, int position) {
        String data = imageData.get(position);
        holder.singleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences mySharedPreferences = context.getSharedPreferences("image_post", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("image_post",data);
                editor.commit();

                if(post_activity.isActivityVisible()){
                    adapter_contentpost.getInstance().setimaget();
                }
                if(editprofile_activity.isActivityVisible()){
                    adapter_editprofile.getInstance().setimaget();
                }


            }
        });
        if (data != null){
            Glide.with(context.getApplicationContext()).load(data).into(holder.singleImageView);

        }else {
            Toast.makeText(context, "Images Empty", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public int getItemCount() {
        return imageData.size();
    }

    public class MyViewHolde extends RecyclerView.ViewHolder {
        ImageView singleImageView;

        public MyViewHolde(View itemView) {
            super(itemView);
            singleImageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}