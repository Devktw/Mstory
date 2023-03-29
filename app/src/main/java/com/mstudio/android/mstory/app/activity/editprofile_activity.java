package com.mstudio.android.mstory.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.adapter.adapter_contentpost;
import com.mstudio.android.mstory.app.adapter.adapter_editprofile;
import com.mstudio.android.mstory.app.adapter.adapter_image;
import com.mstudio.android.mstory.app.adapter.adapter_spiner_image;
import com.mstudio.android.mstory.app.fragment.home_recommend;
import com.mstudio.android.mstory.app.model.User;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class editprofile_activity extends AppCompatActivity {
    CardView exit;
    CardView save_profile;
    ProgressDialog dl;
    Uri imageprofileaccount = null;
    Uri imageprofile = null;
    private static editprofile_activity instance;
    boolean isprofile = false;
    boolean isprofileaccount = false;
    ArrayList<String> imageData;
    ArrayList<String> albumDataData;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    RecyclerView recyclerView;
    RecyclerView reccy_editprofile;
    com.mstudio.android.mstory.app.adapter.adapter_image adapter_image;
    adapter_editprofile adapter_editprofile;
    CardView close_bottom;
    RelativeLayout layoutBottomSheet_image;
    BottomSheetBehavior sheetBehavior_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.editprofile_activity);
        instance = this;
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mStorage = FirebaseStorage.getInstance();
        dl = new ProgressDialog(this);

        reccy_editprofile = (RecyclerView) findViewById(R.id.recy_editprofile);
        reccy_editprofile.setHasFixedSize(true);
        recyclerView = (RecyclerView) findViewById(R.id.rercy_image);
        recyclerView.setHasFixedSize(true);
        adapter_editprofile = new adapter_editprofile(this);
        reccy_editprofile.setAdapter(adapter_editprofile);

        GridLayoutManager myLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(myLayoutManager);
        reccy_editprofile.setLayoutManager(new LinearLayoutManager(editprofile_activity.this,LinearLayoutManager.VERTICAL, false));
        close_bottom = findViewById(R.id.close_bottom);
        layoutBottomSheet_image = findViewById(R.id.bottom_image);
        sheetBehavior_image = BottomSheetBehavior.from(layoutBottomSheet_image);
        sheetBehavior_image.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NotNull View bottomSheet, int newState) {

                switch (newState) {

                    case BottomSheetBehavior.STATE_HIDDEN:

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NotNull View bottomSheet, float slideOffset) {

            }
        });
        close_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
                SharedPreferences sh1 = getSharedPreferences("image_profile", Context.MODE_PRIVATE);
                String profileaccount = sh1.getString("profile_account", "");

                SharedPreferences sh2 = getSharedPreferences("image_profile", Context.MODE_PRIVATE);
                String profile = sh2.getString("profile", "");


            }
        });

        KeyboardVisibilityEvent.setEventListener((editprofile_activity.this),new

                KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged ( boolean isOpen){
                        if (isOpen) {
                            recyclerView.smoothScrollToPosition(2);
                            sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }
                });

        save_profile = findViewById(R.id.save_profile);
        exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        save_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter_editprofile.getInstance().getstring();
                SharedPreferences sh1 = getSharedPreferences("editprofile", Context.MODE_PRIVATE);
                String u = sh1.getString("edittext_username", "");
                String b = sh1.getString("edittext_bio", "");
                String w = sh1.getString("edittext_web", "");

                SharedPreferences mySharedPreferences = getSharedPreferences("image_profile", Context.MODE_PRIVATE);
                String profile = mySharedPreferences.getString("profile", "");
                String profileaccount = mySharedPreferences.getString("profile_account", "");

                imageprofile = Uri.parse(profile);
                imageprofileaccount = Uri.parse(profileaccount);
                File fileprofile = null;
                fileprofile = new File(imageprofile.getPath());

                File fileaccount = null;
                fileaccount = new File(imageprofileaccount.getPath());
                if (!u.equals("")) {
                    if (!(TextUtils.isEmpty(profile) && TextUtils.isEmpty(profileaccount))) {
                        if(isprofileaccount){
                            if (!TextUtils.isEmpty(profileaccount)) {
                                dl.setMessage("กำลังอัพโหลดรูปภาพ...");
                                dl.show();

                                StorageReference filepostt = FirebaseStorage.getInstance().getReference().child("image_user").child(Uri.fromFile(fileaccount).getLastPathSegment());
                                filepostt.putFile(Uri.fromFile(fileaccount)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(Task<Uri> task) {
                                                HashMap<String, Object> hashmap = new HashMap<>();

                                                hashmap.put("user_name", u);
                                                hashmap.put("bio_account", b);
                                                hashmap.put("web_site", w);
                                                hashmap.put("image_url", task.getResult().toString());
                                                mRef.updateChildren(hashmap);
                                                dl.dismiss();
                                                showtoast("บันทึกเรียบร้อย");
                                                isprofileaccount = false;
                                            }

                                        });
                                    }
                                });
                            }
                        }else {

                            if(!isprofile){
                                HashMap<String, Object> hashmap = new HashMap<>();
                                hashmap.put("user_name", u);
                                hashmap.put("bio_account", b);
                                hashmap.put("web_site", w);
                                mRef.updateChildren(hashmap);
                                showtoast("บันทึกเรียบร้อย");
                            }
                        }
                        if(isprofile){
                            if (!TextUtils.isEmpty(profile)) {
                                dl.setMessage("กำลังอัพโหลดรูปภาพ...");
                                dl.show();
                                StorageReference filepost = FirebaseStorage.getInstance().getReference().child("image_user").child(Uri.fromFile(fileprofile).getLastPathSegment());
                                filepost.putFile(Uri.fromFile(fileprofile)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(Task<Uri> task) {

                                                HashMap<String, Object> hashmap = new HashMap<>();

                                                hashmap.put("user_name", u);
                                                hashmap.put("bio_account", b);
                                                hashmap.put("web_site", w);
                                                hashmap.put("image_profile", task.getResult().toString());
                                                mRef.updateChildren(hashmap);
                                                dl.dismiss();
                                                isprofile = false;
                                            }

                                        });
                                    }
                                });
                            }

                        }else {
                            if(!isprofileaccount){
                                HashMap<String, Object> hashmap = new HashMap<>();
                                hashmap.put("user_name", u);
                                hashmap.put("bio_account", b);
                                hashmap.put("web_site", w);
                                mRef.updateChildren(hashmap);
                                showtoast("บันทึกเรียบร้อย");
                            }


                        }

                    } else {

                        HashMap<String, Object> hashmap = new HashMap<>();
                        hashmap.put("user_name", u);
                        hashmap.put("bio_account", b);
                        hashmap.put("web_site", w);
                        mRef.updateChildren(hashmap);
                        showtoast("บันทึกเรียบร้อย");
                    }

                }else {
                    showtoast("ชื่อผู้ใช้ห้ามเว้นว่าง");
                }
            }
        });
        editprofile_activity.RunTask myTask = new editprofile_activity.RunTask();
        myTask.execute();
    }

    private class RunTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            imageData = getAllShownImagesPath(editprofile_activity.this);
            Log.e("imageData: ", String.valueOf(imageData.size()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter_image = new adapter_image(imageData, (editprofile_activity.this));
            // adapter_spiner = new adapter_spiner_image(imageData, post_activity.this);
            // spiner_image.setAdapter((SpinnerAdapter) adapter_spiner);
            recyclerView.setAdapter(adapter_image);
        }
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Collections.reverse(listOfAllImages);
            listOfAllImages.add(absolutePathOfImage);

        }
        return listOfAllImages;
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;

    public static editprofile_activity getInstance() {
        return instance;
    }

    @Override
    protected void onStart() {
        super.onStart();
        sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
        SharedPreferences settings = editprofile_activity.this.getSharedPreferences("image_profile", Context.MODE_PRIVATE);
        settings.edit().remove("profile").commit();
        settings.edit().remove("profile_account").commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //replaces the default 'Back' button action
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(sheetBehavior_image.getState()==BottomSheetBehavior.STATE_COLLAPSED||sheetBehavior_image.getState()==BottomSheetBehavior.STATE_EXPANDED){
                sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
            } else {
                finish();
            }
        }
        return true;
    }

    public void hidekeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void setIsprofile(){
       isprofile = true;
    }
    public void setIsprofileaccount(){
        isprofileaccount = true;
    }
    public void showbottom(){
        sheetBehavior_image.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    public void hidebottom(){
        sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
    public void showtoast(String s) {
        final Toast toast = new Toast(editprofile_activity.this.getApplicationContext());


        LayoutInflater inflater = editprofile_activity.this.getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast,
                (
                        ViewGroup) editprofile_activity.this.findViewById(R.id.customtoast));


        TextView tv = (TextView) layout.findViewById(R.id.tv_toast);
        tv.setText(s);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        editprofile_activity.activityPaused();
        SharedPreferences settings = editprofile_activity.this.getSharedPreferences("image_profile", Context.MODE_PRIVATE);
        settings.edit().remove("profile").commit();
        settings.edit().remove("profile_account").commit();
    }
}
