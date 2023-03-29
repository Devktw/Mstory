package com.mstudio.android.mstory.app.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.graphics.Rect;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mstudio.android.mstory.app.HeightWrappingViewPager;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.activity.LoginActivity;
import com.mstudio.android.mstory.app.activity.details_profile_activity;
import com.mstudio.android.mstory.app.activity.editprofile_activity;
import com.mstudio.android.mstory.app.activity.post_activity;
import com.mstudio.android.mstory.app.activity.profile_activity;
import com.mstudio.android.mstory.app.fragment.account_frag;
import com.mstudio.android.mstory.app.fragment.image_frag;
import com.mstudio.android.mstory.app.fragment.like_frag;
import com.mstudio.android.mstory.app.fragment.post_frag;
import com.mstudio.android.mstory.app.model.Post;
import com.mstudio.android.mstory.app.model.User;

import android.widget.RelativeLayout;
import android.view.View.OnClickListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class adapter_editprofile extends RecyclerView.Adapter<adapter_editprofile.CustomViewHolder> {
    private Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;
    EditText editText_username;
    EditText editText_bio;
    EditText editText_web;
    ImageView image_profileaccount;
    ImageView image_profile;
    ImageView edit_profileaccount;
    ImageView edit_profile;


    ArrayList<String> imageData;
    ArrayList<String> albumDataData;

    RecyclerView recyclerView;
    com.mstudio.android.mstory.app.adapter.adapter_image adapter_image;
    Spinner spiner_image;
    adapter_spiner_image adapter_spiner;


    boolean notselectimage ;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST = 1;
    private int overallXScroll = 0;
    private int RESULT_LOAD_IMG = 1;

    boolean isprofile;
    boolean isprofileaccount;
    ProgressDialog dl;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    private static adapter_editprofile instance;
    public adapter_editprofile(Context context) {
        this.mContext = context;
        instance=this;
        getInfo();
    }


    @Override
    public  adapter_editprofile.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.editprofile, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int position) {

    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static adapter_editprofile getInstance() {
        return instance;
    }



    public void requestPermissionForCamera() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = mContext.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }


    public void setimaget() {
        editprofile_activity.getInstance().hidebottom();
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("image_post", Context.MODE_PRIVATE);
        String data = mySharedPreferences.getString("image_post", "");
        Uri imageUri = Uri.parse(data);
        if(isprofile){
            editprofile_activity.getInstance().setIsprofile();
            image_profile.setImageURI(imageUri);
            SharedPreferences sh = mContext.getSharedPreferences("image_profile", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sh.edit();
            editor.putString("profile",data);
            editor.commit();
        }
        if(isprofileaccount){
            editprofile_activity.getInstance().setIsprofileaccount();
            image_profileaccount.setImageURI(imageUri);
            SharedPreferences sh2 = mContext.getSharedPreferences("image_profile", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sh2.edit();
            editor.putString("profile_account",data);
            editor.commit();
        }

    }
    public void getstring() {
        String u = editText_username.getText().toString();
        String b = editText_bio.getText().toString();
        String w = editText_web.getText().toString();

        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("editprofile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("edittext_username",u.toString());
        editor.putString("edittext_bio",b.toString());
        editor.putString("edittext_web",w.toString());
        editor.commit();
    }

    private void getInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                editText_username.setText(user.getUser_name());
                editText_bio.setText(user.getBio_account());
                SharedPreferences mySharedPreferences = mContext.getSharedPreferences("image_user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("image_profile", user.getImage_profile());
                editor.putString("image_profile_account", user.getImage_url());
                editor.commit();
                if (isValidContextForGlide(mContext)){
                    Glide.with(mContext).load(user.getImage_url()).into(image_profileaccount);
                    Glide.with(mContext).load(user.getImage_profile()).into(image_profile);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
    class CustomViewHolder extends RecyclerView.ViewHolder {

        public CustomViewHolder(View item) {
            super(item);
            mContext = item.getContext();
            dl = new ProgressDialog(mContext);
            editprofile_activity.activityResumed();
            mDatabase = FirebaseDatabase.getInstance();
            mRef=mDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mStorage=FirebaseStorage.getInstance();
            editText_username = item.findViewById(R.id.edittext_username);
            editText_bio = item.findViewById(R.id.edttext_bio);
            editText_web = item.findViewById(R.id.edttext_web);
            edit_profileaccount = item.findViewById(R.id.edit_profileaccount);
            edit_profile = item.findViewById(R.id.edit_pofile);
            image_profile = item.findViewById(R.id.image_profile);
            image_profileaccount = item.findViewById(R.id.image_account);



            edit_profileaccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isprofileaccount = true;
                    isprofile = false;
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            editprofile_activity.getInstance().showbottom();
                            editprofile_activity.getInstance().hidekeyboard();
                        }
                    }, 50);
                }
            });
            edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isprofileaccount = false;
                    isprofile = true;
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            editprofile_activity.getInstance().showbottom();
                            editprofile_activity.getInstance().hidekeyboard();
                        }
                    }, 50);
                }
            });
            editText_username.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    if (s.equals("")){
                        //SharedPreferences settings = mContext.getSharedPreferences("editprofile", Context.MODE_PRIVATE);
                       // settings.edit().remove("edittext_username").commit();
                        //settings.edit().remove("edittext_bio").commit();
                       // settings.edit().remove("edittext_web").commit();
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });
            PackageManager pm = mContext.getPackageManager();
            int hasStorage = pm.checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    mContext.getPackageName());
            int hasCamera = pm.checkPermission(
                    Manifest.permission.CAMERA,
                    mContext.getPackageName());
            if (hasStorage!= PackageManager.PERMISSION_GRANTED) {
                try {
                    requestPermissionForReadExtertalStorage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (hasCamera!= PackageManager.PERMISSION_GRANTED) {
                try {
                    requestPermissionForCamera();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
