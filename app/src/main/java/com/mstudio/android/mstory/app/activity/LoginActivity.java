package com.mstudio.android.mstory.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
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
import org.jetbrains.annotations.Nullable;

public class LoginActivity extends AppCompatActivity {
    private CardView btn_google;
    public static final int RC_SIGN_IN = 1;
    public FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    CheckBox checkbox_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.login);
        checkbox_login=findViewById(R.id.checkbox_login);
        com.google.firebase.FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkbox_login.isChecked()) {

                    try {
                        Intent choose = mGoogleSignInClient.getSignInIntent();
                        startActivityForResult(choose, RC_SIGN_IN);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }else {
                        Toast.makeText(getApplicationContext(), "ดำเนินการยอมรับเงื่อนไขการให้บริการก่อน", Toast.LENGTH_LONG).show();
                }
            }
        });
        FirebaseUser iscurrentUser = mAuth.getCurrentUser();
        if (iscurrentUser != null) {
            Intent i = new Intent(this,main.class);
            startActivity(i);
            finish();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Intent i = new Intent(getApplicationContext(), main.class);
                            startActivity(i);
                            finish();
                            FirebaseUser userData = FirebaseAuth.getInstance().getCurrentUser();
                            Toast.makeText(getApplicationContext(), "ยินดีต้อนรับ "+userData.getDisplayName(), Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "เกิดข้อผิกพลาด กรุณาเช็คเครือข่ายแล้วลองอีกครั้ง", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        //Done dont forget to subscribe and share with friends love you all
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed
                Toast.makeText(getApplicationContext(),"Google sign in failed " +e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}