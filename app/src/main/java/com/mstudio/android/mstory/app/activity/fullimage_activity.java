package com.mstudio.android.mstory.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.ImageDownloader;
import com.mstudio.android.mstory.app.MyDownloadService;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.model.Post;
import com.mstudio.android.mstory.app.model.User;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class fullimage_activity extends AppCompatActivity {
    CardView menu_image;
    CardView exit;
    TextView value_like;
    TextView value_comment;
    TextView value_view;
    TextView value_send;
    ImageView like;
    CardView viewpost;
    ImageView image;
    CardView gocomment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullimage_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(150);
            getWindow().getSharedElementReturnTransition().setDuration(150)
                    .setInterpolator(new DecelerateInterpolator());
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int flags =getWindow().getDecorView().getSystemUiVisibility();
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                getWindow().getDecorView().setSystemUiVisibility(flags);
            }
        }
        String post_id =getIntent().getStringExtra("post_id");
        String publisher = getIntent().getStringExtra("publisher");
        exit = findViewById(R.id.exit);
        menu_image = findViewById(R.id.menu_image);
        like =findViewById(R.id.like);
        image = findViewById(R.id.image);
        value_like = findViewById(R.id.value_like);
        value_view = findViewById(R.id.value_view);
        value_comment = findViewById(R.id.value_comment);
        value_send = findViewById(R.id.value_send);
        viewpost = findViewById(R.id.viewpost);
        gocomment = findViewById(R.id.gocomment2);

        gocomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(fullimage_activity.this, showall_post_activity.class);
                i.putExtra("post_id", post_id);
                i.putExtra("publisher", publisher);
                i.putExtra("gocomment", "1");
                startActivity(i);
            }
        });
        menu_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showmenu_image(post_id);
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAfterTransition();
            }
        });
        ActivityCompat.postponeEnterTransition(this);
        DatabaseReference rff = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id);
        rff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Glide.with(getApplicationContext()).load(post.getImage_post()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ActivityCompat.startPostponedEnterTransition(fullimage_activity.this);
                        return false;
                    }
                }).into(image);

            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("viewpost").child(post_id);
        rf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Animation animation = AnimationUtils.loadAnimation(fullimage_activity.this, R.anim.slide_in_bottom);
                value_view.startAnimation(animation);
                value_view.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
        like(post_id,like,value_like);
        urlike(value_like,post_id);
        value_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post_id).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    Animation animation = AnimationUtils.loadAnimation(fullimage_activity.this, R.anim.slide_in_bottom);
                    value_like.startAnimation(animation);
                    value_like.setTextColor(fullimage_activity.this.getResources().getColor(R.color.like));
                }else {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post_id).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    Animation animation = AnimationUtils.loadAnimation(fullimage_activity.this, R.anim.slide_in_bottom);
                    value_like.startAnimation(animation);
                    value_like.setTextColor(fullimage_activity.this.getResources().getColor(R.color.white));
                }
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post_id).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    Animation animation = AnimationUtils.loadAnimation(fullimage_activity.this, R.anim.slide_in_bottom);
                    value_like.startAnimation(animation);
                    value_like.setTextColor(fullimage_activity.this.getResources().getColor(R.color.like));
                }else {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post_id).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    Animation animation = AnimationUtils.loadAnimation(fullimage_activity.this, R.anim.slide_in_bottom);
                    value_like.startAnimation(animation);
                    value_like.setTextColor(fullimage_activity.this.getResources().getColor(R.color.tint_post));
                }
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id).child("Comment");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Animation animation = AnimationUtils.loadAnimation(fullimage_activity.this, R.anim.slide_in_bottom);
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
                showmenu_view_post(post_id,publisher);
            }
        });
    }
    public void showmenu_view_post(String post_id,String publisher) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_viewpost, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fullimage_activity.this, R.style.SheetDialogImage);
        TextView tv_value_viewpost;
        tv_value_viewpost = bottomSheetView.findViewById(R.id.tv_valueview_post);
        bottomSheetDialog.setContentView(bottomSheetView);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("viewpost").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Animation animation = AnimationUtils.loadAnimation(fullimage_activity.this, R.anim.slide_in_bottom);
                tv_value_viewpost.startAnimation(animation);
                tv_value_viewpost.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });


        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // do something
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do something
            }
        });
        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@android.support.annotation.NonNull View bottomSheet, int newState) {
                // do something
            }

            @Override
            public void onSlide(@android.support.annotation.NonNull View bottomSheet, float slideOffset) {
                // do something
            }
        };
        bottomSheetDialog.show();
    }
    private Transition enterTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(500);

        return bounds;
    }

    private Transition returnTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setInterpolator(new DecelerateInterpolator());
        bounds.setDuration(500);

        return bounds;
    }


    private void like(String post_id,ImageView like,TextView like_tv){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("likes").child(post_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    like.setImageResource(R.drawable.ic_like_true);
                    like.setColorFilter(ContextCompat.getColor(fullimage_activity.this, R.color.like));
                    like.setTag("liked");
                    like_tv.setTextColor(fullimage_activity.this.getResources().getColor(R.color.like));
                }else {
                    like.setImageResource(R.drawable.ic_like_false);
                    like.setColorFilter(ContextCompat.getColor(fullimage_activity.this, R.color.white));
                    like.setTag("like");
                    like_tv.setTextColor(fullimage_activity.this.getResources().getColor(R.color.white));
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
                Animation animation = AnimationUtils.loadAnimation(fullimage_activity.this, R.anim.slide_in_bottom);
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
                Animation animation = AnimationUtils.loadAnimation(fullimage_activity.this, R.anim.slide_in_bottom);
                value_view.startAnimation(animation);
                value_view.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

    }
    public void showmenu_image(String post_id) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_image, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fullimage_activity.this, R.style.SheetDialogImage);
        LinearLayout saveimage = bottomSheetView.findViewById(R.id.saveimage);
        saveimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(post_id).child("image_post");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                       Post post = snapshot.getValue(Post.class);
                       new DownloadImage().execute(post.getImage_post());
                    }
                    @Override
                    public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                    }
                });
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // do something
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // do something
            }
        });
        BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@android.support.annotation.NonNull View bottomSheet, int newState) {
                // do something
            }

            @Override
            public void onSlide(@android.support.annotation.NonNull View bottomSheet, float slideOffset) {
                // do something
            }
        };
        bottomSheetDialog.show();
    }



    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(getApplicationContext(), result, "Mstory_image.png");
        }
    }
    public void saveImage(Context context, Bitmap b, String imageName)
    {
        FileOutputStream foStream;
        try
        {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        }
        catch (Exception e)
        {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }
}
