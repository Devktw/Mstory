package com.mstudio.android.mstory.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mstudio.android.mstory.app.R;

import java.util.ArrayList;

public class adapter_spiner_image extends RecyclerView.Adapter<adapter_spiner_image.MyViewHolde> {
    Context context;
    private ArrayList<String> imageData = new ArrayList<String>();


    public adapter_spiner_image(ArrayList<String> imageData, FragmentActivity activity) {
        this.imageData = imageData;
        this.context = activity;
    }

    @Override
    public MyViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_image, parent, false);

        return new MyViewHolde(itemView);
    }

    @Override
    public void onBindViewHolder(adapter_spiner_image.MyViewHolde holder, int position) {
        String data = imageData.get(position);
        if (data != null){


        }else {
            Toast.makeText(context, "Images Empty", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public int getItemCount() {
        return imageData.size();
    }

    public class MyViewHolde extends RecyclerView.ViewHolder {
        TextView tv;

        public MyViewHolde(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }
}