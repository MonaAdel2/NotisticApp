package com.example.notisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NoteActivity extends AppCompatActivity {

    ImageButton backBtn, saveBtn;
    TextInputEditText titleEt, descriptionInputEt;
    AppCompatButton wSaveBtn, wDiscardBtn;

    Dialog warningDialog;
    FirebaseFirestore db ;
    FirebaseAuth auth;
    FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        backBtn = findViewById(R.id.img_btn_back_note);
        saveBtn = findViewById(R.id.img_btn_save_note);
        titleEt = findViewById(R.id.et_title_note);
        descriptionInputEt = findViewById(R.id.et_description_note);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEt.getText().toString();
                String desc = descriptionInputEt.getText().toString();

                if(title.isEmpty()){
                    Toast.makeText(NoteActivity.this, "The title field is required", Toast.LENGTH_SHORT).show();
                }else{
                    DocumentReference reference = db.collection("notes")
                            .document(user.getUid()).collection("myNotes").document();

                    Map<String, Object> note = new HashMap<>();
                    note.put("title", title);
                    note.put("description", desc);

                    reference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(NoteActivity.this, "The note is created successfully", Toast.LENGTH_SHORT).show();
                            // intent to main activity
                            startActivity(new Intent(NoteActivity.this, MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NoteActivity.this, "Failed to create the note", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEt.getText().toString();
                String desc = descriptionInputEt.getText().toString();

                // dialog alarm save or not
                if(!title.isEmpty()){
                    warningDialog = new Dialog(NoteActivity.this);
                    warningDialog.setContentView(R.layout.save_dialog_custom);
                    wDiscardBtn = warningDialog.findViewById(R.id.discard_btn_dialog);
                    wSaveBtn = warningDialog.findViewById(R.id.save_btn_dialog);
                    warningDialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                    warningDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_button));
                    warningDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    warningDialog.show();
                    warningDialogDisplay(title, desc);
                }
                // Empty title--> no note saved
                else{
                    startActivity(new Intent(NoteActivity.this, MainActivity.class));
                    finish();
                }

            }
        });
    }

    private void warningDialogDisplay(String title, String desc){
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        LayoutInflater layoutInflater = this.getLayoutInflater();
//        alert.setView(layoutInflater.inflate(R.layout.save_dialog_custom, null)).create().show();
        wSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningDialog.dismiss();
                saveNote(title, desc);
            }
        });

        wDiscardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningDialog.dismiss();
                startActivity(new Intent(NoteActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    public void saveNote(String title, String desc){
        DocumentReference reference = db.collection("notes")
                .document(user.getUid()).collection("myNotes").document();

        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("description", desc);

        reference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(NoteActivity.this, "The note is created successfully", Toast.LENGTH_SHORT).show();
                // intent to main activity
                startActivity(new Intent(NoteActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NoteActivity.this, "Failed to create the note", Toast.LENGTH_SHORT).show();
            }
        });

    }
}