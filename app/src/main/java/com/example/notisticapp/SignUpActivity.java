package com.example.notisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";

    EditText etEmail, etPassword;
    AppCompatButton btnSignup;
    TextView tvResult, tvLogin;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etEmail = findViewById(R.id.etEmail_signup);
        etPassword = findViewById(R.id.etPassword_signup);
        btnSignup = findViewById(R.id.btnSignUp_signup);
        tvLogin = findViewById(R.id.tv_login_signup);

        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(email.isEmpty()){
                    etEmail.setError("This Field can't be empty");
                }
                if(password.isEmpty()){
                    etPassword.setError("This Field can't be empty");
                }
                else{
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "The user is created", Toast.LENGTH_SHORT).show();
                                sendVerificationEmail();
                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "Sign up is failed", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, task.getResult().toString());

                            }
                        }
                    });
                }
            }

        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this, "Verfication Email is sent, Verify your email and then login", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                        finish();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));

                    }else{
                        Toast.makeText(SignUpActivity.this, "Failed to send the Verfication Email, Try again", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
}