package com.mstudio.android.mstory.app.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.common.logging.Logger;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mstudio.android.mstory.app.OnPhoneImagesObtained;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.adapter.adapter_account;
import com.mstudio.android.mstory.app.adapter.adapter_contentpost;
import com.mstudio.android.mstory.app.adapter.adapter_image;
import com.mstudio.android.mstory.app.adapter.adapter_spiner_image;
import com.mstudio.android.mstory.app.fragment.home_recommend;
import com.mstudio.android.mstory.app.model.PhoneAlbum;
import com.mstudio.android.mstory.app.model.PhonePhoto;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;

public class post_activity extends AppCompatActivity {
    private static final int RCODE_PERMISSION_ALL = 1;
    private static post_activity instance;

    ProgressDialog dl;
    CardView btn_post;
    CardView addimage;
    CardView camera;
    CardView exit;
    CardView close_bottom;
    View line_bar_post;
    ArrayList<String> imageData;
    ArrayList<String> albumDataData;
    RelativeLayout layoutBottomSheet_image;
    BottomSheetBehavior sheetBehavior_image;
    RecyclerView recyclerView;
    adapter_image adapter_image;
    Spinner spiner_image;
    adapter_spiner_image adapter_spiner;
    RecyclerView recy_contentpost;
    adapter_contentpost adapter_contentpost;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    Uri imageUri = null;
    boolean notselectimage ;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST = 1;
    private int overallXScroll = 0;
    private static int RESULT_LOAD_IMG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);
        instance =this;
        post_activity.activityResumed();
        mDatabase = FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference().child("Posts");
        mStorage=FirebaseStorage.getInstance();
        dl = new ProgressDialog(this);
        close_bottom = findViewById(R.id.close_bottom);
        btn_post = findViewById(R.id.btn_post);
        line_bar_post = findViewById(R.id.line_bar_post);
        recy_contentpost=findViewById(R.id.recy_contentpost);

        adapter_contentpost = new adapter_contentpost(this);
        recy_contentpost.setAdapter(adapter_contentpost);
        addimage = findViewById(R.id.addimage);
        camera = findViewById(R.id.cameraimage);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        spiner_image = findViewById(R.id.spiner_image);
        exit = findViewById(R.id.exit);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        layoutBottomSheet_image = findViewById(R.id.bottom_image);
        sheetBehavior_image = BottomSheetBehavior.from(layoutBottomSheet_image);
        sheetBehavior_image.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NotNull View bottomSheet, int newState) {

                switch (newState) {

                    case BottomSheetBehavior.STATE_HIDDEN:
                        notselectimage =false;
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        notselectimage =true;
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        notselectimage =true;
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
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {


                    public void run() {
                        sheetBehavior_image.setState(BottomSheetBehavior.STATE_EXPANDED);
                        hidekeyboard();
                    }
                }, 50);




            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter_contentpost.getInstance().getstring();

                SharedPreferences myShared = getSharedPreferences("edittext_post", Context.MODE_PRIVATE);
                String datatext = myShared.getString("edittext_post", "");
                SharedPreferences mySharedPreferences = getSharedPreferences("image_post", Context.MODE_PRIVATE);
                String image_post = mySharedPreferences.getString("image_post", "");

                imageUri = Uri.parse(image_post);
                File file = null;
                file = new File(imageUri.getPath());


                if (!(TextUtils.isEmpty(image_post) && TextUtils.isEmpty(image_post))) {
                    if (TextUtils.isEmpty(datatext)) {
                        if (!TextUtils.isEmpty(image_post)) {
                            dl.setMessage("กำลังโพสต์...");
                            dl.show();
                            StorageReference filepost = mStorage.getReference().child("image_post").child(Uri.fromFile(file).getLastPathSegment());
                            filepost.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(Task<Uri> task) {





                                            String postid = mRef.push().getKey();
                                            String publisher = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                                            String date_time = date + " • " + time;
                                            HashMap<String,Object> hashmap = new HashMap<>();
                                            hashmap.put("id",postid);
                                            hashmap.put("image_post",task.getResult().toString());
                                            hashmap.put("cap_post",datatext);
                                            hashmap.put("publisher",publisher);
                                            hashmap.put("date_time",date_time);
                                            mRef.child(postid).setValue(hashmap);
                                            new Handler().postDelayed(new Runnable() {
                                                public void run() {
                                                    home_recommend.getInstance().showtoast("แชร์โพสต์ของคุณแล้ว");
                                                }
                                            }, 800);

                                            dl.dismiss();
                                            sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
                                            finish();


                                        }

                                    });
                                }
                            });

                        }
                    }
                    if (!TextUtils.isEmpty(datatext)) {

                        dl.setMessage("กำลังโพสต์...");
                        dl.show();
                        StorageReference filepostt = mStorage.getReference().child("image_post").child(Uri.fromFile(file).getLastPathSegment());
                        filepostt.putFile(Uri.fromFile(file)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(Task<Uri> task) {
                                        String postid = mRef.push().getKey();
                                        String publisher = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                                        String date_time = date + " • " + time;
                                        HashMap<String,Object> hashmap = new HashMap<>();
                                        hashmap.put("id",postid);
                                        hashmap.put("image_post",task.getResult().toString());
                                        hashmap.put("cap_post",datatext);
                                        hashmap.put("publisher",publisher);
                                        hashmap.put("date_time",date_time);
                                        mRef.child(postid).setValue(hashmap);
                                        new Handler().postDelayed(new Runnable() {
                                            public void run() {
                                                home_recommend.getInstance().showtoast("แชร์โพสต์ของคุณแล้ว");
                                            }
                                        }, 800);
                                        dl.dismiss();
                                        sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
                                        finish();


                                    }

                                });
                            }
                        });
                    }



                } else {
                    if (!TextUtils.isEmpty(datatext)) {
                        String postid = mRef.push().getKey();
                        String publisher = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                        String date_time = date + " • " + time;
                        HashMap<String,Object> hashmap = new HashMap<>();
                        hashmap.put("id",postid);
                        hashmap.put("cap_post",datatext);
                        hashmap.put("publisher",publisher);
                        hashmap.put("date_time",date_time);
                        mRef.child(postid).setValue(hashmap);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                home_recommend.getInstance().showtoast("แชร์โพสต์ของคุณแล้ว");
                            }
                        }, 800);
                        dl.dismiss();
                        sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
                        finish();

                    }
                    if (!TextUtils.isEmpty(image_post)) {
                        String postid = mRef.push().getKey();
                        String publisher = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                        String date_time = date + " • " + time;
                        HashMap<String,Object> hashmap = new HashMap<>();
                        hashmap.put("id",postid);
                        hashmap.put("cap_post",datatext);
                        hashmap.put("publisher",publisher);
                        hashmap.put("date_time",date_time);
                        mRef.child(postid).setValue(hashmap);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                home_recommend.getInstance().showtoast("แชร์โพสต์ของคุณแล้ว");
                            }
                        }, 800);
                        dl.dismiss();
                        sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
                        finish();

                    }

                }
            }


            });

        close_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.rercy_image);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager myLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(myLayoutManager);
        PackageManager pm = this.getPackageManager();
        int hasStorage = pm.checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                this.getPackageName());
        int hasCamera = pm.checkPermission(
                Manifest.permission.CAMERA,
                this.getPackageName());
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

        recy_contentpost.setLayoutManager(new LinearLayoutManager(post_activity.this,LinearLayoutManager.VERTICAL, false));
        recy_contentpost.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                overallXScroll = overallXScroll + dx;
                Log.i("check","overall X  = " + overallXScroll);
                LinearLayoutManager myLayoutManager = (LinearLayoutManager) recy_contentpost.getLayoutManager();
                recy_contentpost.setLayoutManager(myLayoutManager);
                int scrollPosition = myLayoutManager.findFirstVisibleItemPosition();
                if (recy_contentpost.canScrollVertically(-1)) {
                    line_bar_post.setVisibility(View.VISIBLE);
                }else{
                    line_bar_post.setVisibility(View.GONE);
                }
            }
        });
        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        RunTask myTask = new RunTask();
        myTask.execute();
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode) {

                case RCODE_PERMISSION_ALL: {

                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        RunTask myTask = new RunTask();
                        myTask.execute();
                    } else {
                       
                    }
                    return;
                }
        }}
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent dataintent) {
        super.onActivityResult(reqCode, resultCode, dataintent);
            if (reqCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
               // string base64String = Convert.ToBase64String(imageBytes);
                SharedPreferences mySharedPreferences = getSharedPreferences("image_post", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
               // editor.putString("image_post", photo.toString());
                editor.commit();
                //Toast.makeText(post_activity.this,data.getPath().toString(),Toast.LENGTH_SHORT).show();
                adapter_contentpost.setimaget();
        }
    }


    public void hidekeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static post_activity getInstance() {
        return instance;
    }
    public void hidebottom() {
        sheetBehavior_image.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //replaces the default 'Back' button action
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (sheetBehavior_image.getState() == BottomSheetBehavior.STATE_EXPANDED || sheetBehavior_image.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
            } else {
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences settings = this.getSharedPreferences("edittext_post", Context.MODE_PRIVATE);
        settings.edit().remove("edittext_post").commit();
        if (sheetBehavior_image.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior_image.setState(BottomSheetBehavior.STATE_HIDDEN);
            notselectimage = false;
        }
    }
    public void requestPermissionForCamera() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.CAMERA},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    private class RunTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            imageData = getAllShownImagesPath(post_activity.this);
            Log.e("imageData: ", String.valueOf(imageData.size()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter_image = new adapter_image(imageData, post_activity.this);
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
    public static void getPhoneAlbums(Context context , OnPhoneImagesObtained listener ){
        // Creating vectors to hold the final albums objects and albums names
        Vector<PhoneAlbum> phoneAlbums = new Vector<>();
        Vector< String > albumsNames = new Vector<>();

        // which image properties are we querying
        String[] projection = new String[] {
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
        };

        // content: style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        Cursor cur = context.getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        if ( cur != null && cur.getCount() > 0 ) {
            Log.i("DeviceImageManager"," query count=" + cur.getCount());

            if (cur.moveToFirst()) {
                String bucketName;
                String data;
                String imageId;
                int bucketNameColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int imageUriColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                int imageIdColumn = cur.getColumnIndex(
                        MediaStore.Images.Media._ID );

                do {
                    // Get the field values
                    bucketName = cur.getString( bucketNameColumn );
                    data = cur.getString( imageUriColumn );
                    imageId = cur.getString( imageIdColumn );

                    // Adding a new PhonePhoto object to phonePhotos vector
                    PhonePhoto phonePhoto = new PhonePhoto();
                    phonePhoto.setAlbumName( bucketName );
                    phonePhoto.setPhotoUri( data );
                    phonePhoto.setId( Integer.valueOf( imageId ) );

                    if ( albumsNames.contains( bucketName ) ) {
                        for ( PhoneAlbum album : phoneAlbums ) {
                            if ( album.getName().equals( bucketName ) ) {
                                album.getAlbumPhotos().add( phonePhoto );
                                Log.i( "DeviceImageManager", "A photo was added to album => " + bucketName );
                                break;
                            }
                        }
                    } else {
                        PhoneAlbum album = new PhoneAlbum();
                        Log.i( "DeviceImageManager", "A new album was created => " + bucketName );
                        album.setId( phonePhoto.getId() );
                        album.setName( bucketName );
                        album.setCoverUri( phonePhoto.getPhotoUri() );
                        album.getAlbumPhotos().add( phonePhoto );
                        Log.i( "DeviceImageManager", "A photo was added to album => " + bucketName );

                        phoneAlbums.add( album );
                        albumsNames.add( bucketName );
                    }

                } while (cur.moveToNext());
            }

            cur.close();
            listener.onComplete( phoneAlbums );
        } else {
            listener.onError();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = this.getSharedPreferences("image_post", Context.MODE_PRIVATE);
        settings.edit().remove("image_post").commit();
        post_activity.activityPaused();
    }
    public void showtoast(String s) {
        final Toast toast = new Toast(post_activity.this.getApplicationContext());


        LayoutInflater inflater = post_activity.this.getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast,
                (
                        ViewGroup) post_activity.this.findViewById(R.id.customtoast));


        TextView tv = (TextView) layout.findViewById(R.id.tv_toast);
        tv.setText(s);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
