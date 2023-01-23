package com.example.notisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText etRecEmail;
    TextView tvLogin;
    AppCompatButton btnRecover;

    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        etRecEmail = findViewById(R.id.et_email_forget_pass);
        tvLogin = findViewById(R.id.tv_login_forget_pass);
        btnRecover = findViewById(R.id.btn_recover_forget_pass);

        auth = FirebaseAuth.getInstance();


        btnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etRecEmail.getText().toString().trim();
                if(email.isEmpty()){
                    etRecEmail.setError("Please Enter the Email");
                }else{
                    // Recovery the password
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isComplete()){
                                Toast.makeText(ForgetPasswordActivity.this, "An Email has sent, check it and then login", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                            }
                            else{
                                Toast.makeText(ForgetPasswordActivity.this, "Email is wrong, or Account doesn't exists", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
            }
        });

    }
}