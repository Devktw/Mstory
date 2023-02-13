package com.mstudio.android.mstory.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mstudio.android.mstory.app.R;

public class post_activity extends AppCompatActivity {
    ImageView image_account;
    CardView exit;
    TextView username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);
        image_account=findViewById(R.id.image_account);
        username=findViewById(R.id.username);
        exit=findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        FirebaseUser userData = FirebaseAuth.getInstance().getCurrentUser();
        if ((FirebaseAuth.getInstance().getCurrentUser() != null)) {
            username.setText(userData.getDisplayName().toString());
            Glide.with(this).load(userData.getPhotoUrl().toString()).into(image_account);

        }
    }
}
