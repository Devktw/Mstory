package com.mstudio.android.mstory.app.activity;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mstudio.android.mstory.app.R;
import com.mstudio.android.mstory.app.main;

public class login_mstory_activity extends AppCompatActivity {

    CheckBox checkbox_login;
    CardView btn_mstory;
    EditText edittext_email;
    EditText edittext_password;
    String username,password;
    FirebaseAuth auth;
    ProgressDialog dl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.login_mstory_activity);
        auth = FirebaseAuth.getInstance();

        edittext_email = findViewById(R.id.edittext_email);
        edittext_password = findViewById(R.id.edittext_password);
        btn_mstory = findViewById(R.id.btn_mstory);
        checkbox_login=findViewById(R.id.checkbox_login);
        btn_mstory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkbox_login.isChecked()) {
                    dl = new ProgressDialog(login_mstory_activity.this);
                    dl.setMessage("กรุณารอสักครู่");
                    dl.show();
                    String str_email = edittext_email.getText().toString();
                    String str_pass = edittext_password.getText().toString();

                    if(TextUtils.isEmpty(str_email)|| TextUtils.isEmpty(str_pass)){
                        showtoast("กรุณากรอกให้ครบ");
                    }else {
                        auth.signInWithEmailAndPassword(str_email,str_pass).addOnCompleteListener(login_mstory_activity.this ,new OnCompleteListener() {
                            @Override
                            public void onComplete(Task task) {
                                if(task.isSuccessful()){
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            dl.dismiss();
                                            Intent i = new Intent(getApplicationContext(), main.class);
                                            startActivity(i);
                                            finish();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            dl.dismiss();
                                        }
                                    });
                                }else {
                                    dl.dismiss();
                                    showtoast("เกิดข้อผิดพลาด");
                                }
                            }
                        });
                    }
                }else {
                    showtoast("ดำเนินการยอมรับเงื่อนไขการให้บริการก่อน");
                }
            }
        });
    }
    public void showtoast(String s) {
        final Toast toast = new Toast(login_mstory_activity.this.getApplicationContext());


        LayoutInflater inflater = login_mstory_activity.this.getLayoutInflater();

        View layout = inflater.inflate(R.layout.custom_toast,
                (
                        ViewGroup) login_mstory_activity.this.findViewById(R.id.customtoast));


        TextView tv = (TextView) layout.findViewById(R.id.tv_toast);
        tv.setText(s);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}