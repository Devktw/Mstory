package com.mstudio.android.mstory.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.mstudio.android.mstory.app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mstudio.android.mstory.app.main;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private CardView btn_google;
    public static final int RC_SIGN_IN = 1;
    public FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    CheckBox checkbox_login;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;
    CardView btn_register_mstory;
    TextView btn_mstory;
    EditText edittext_username;
    EditText edittext_email;
    EditText edittext_password;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog dl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.login);
        mDatabase = FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference().child("account");
        mStorage=FirebaseStorage.getInstance();
        checkbox_login=findViewById(R.id.checkbox_login);
        btn_mstory=findViewById(R.id.btn_mstory);
        auth = FirebaseAuth.getInstance();
        edittext_username = findViewById(R.id.edittext_username);
        edittext_password = findViewById(R.id.edittext_password);
        edittext_email = findViewById(R.id.edittext_email);
        btn_mstory = findViewById(R.id.btn_mstory);
        mAuth = FirebaseAuth.getInstance();

        btn_register_mstory = findViewById(R.id.btn_register_mstory);
        btn_register_mstory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkbox_login.isChecked()) {
                    dl = new ProgressDialog(LoginActivity.this);
                    dl.setMessage("กรุณารอสักครู่...");
                    dl.show();
                    String strusername = edittext_username.getText().toString();
                    String stremail = edittext_email.getText().toString();
                    String strpassword = edittext_password.getText().toString();
                    if(TextUtils.isEmpty(strusername)||TextUtils.isEmpty(stremail)||TextUtils.isEmpty(strpassword)){
                        showtoast("กรุณากรอกให้ครบ");
                    }else if(strpassword.length()<6){
                        showtoast("รหัสผ่านต้องมี6ตัวขึ้นไป");
                    }else {
                        register_mstory(strusername,stremail,strpassword);
                    }
                    try {

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    showtoast("ดำเนินการยอมรับเงื่อนไขการให้บริการก่อน");
                }
            }
        });
        btn_mstory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, login_mstory_activity.class);
                startActivity(i);
            }
        });
        FirebaseUser iscurrentUser = mAuth.getCurrentUser();
        if (iscurrentUser != null) {
            Intent i = new Intent(this,main.class);
            startActivity(i);
            finish();
        }
    }

    private void  register_mstory(String username,String email,String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser userData = auth.getInstance().getCurrentUser();
                    String id = userData.getUid();
                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
                    HashMap<String,Object> hashmap = new HashMap<>();
                    hashmap.put("id",id);
                    hashmap.put("user_name",username.toLowerCase());
                    hashmap.put("email_account",email);
                    hashmap.put("bio_account","-");
                    hashmap.put("image_profile","");
                    hashmap.put("verified","");
                    hashmap.put("image_url","https://firebasestorage.googleapis.com/v0/b/mstory-5bee1.appspot.com/o/profile_noload.png?alt=media&token=5e9f2e8c-74f2-4fee-b324-4622c8a41149");
                    reference.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if(task.isSuccessful()){
                                dl.dismiss();
                                Intent i = new Intent(LoginActivity.this, main.class);
                                startActivity(i);
                                finish();
                            }

                        }
                    });

                }else {
                    dl.dismiss();
                    showtoast("เกิดข้อผิดพลาด");
                }
            }
        });

    }
    public void showtoast(String s) {
        final Toast toast = new Toast(LoginActivity.this.getApplicationContext());


        LayoutInflater inflater = LoginActivity.this.getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast,
                (
                        ViewGroup) LoginActivity.this.findViewById(R.id.customtoast));


        TextView tv = (TextView) layout.findViewById(R.id.tv_toast);
        tv.setText(s);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}