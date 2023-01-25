package com.example.notisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    FirebaseFirestore db ;
    FirebaseAuth auth;
    FirebaseUser user;

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
                startActivity(new Intent(NoteActivity.this, MainActivity.class));
            }
        });
    }
}