package com.example.notisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    public static final String TAG = "EditNoteActivity";

    ImageButton backBtn, saveBtn;
    TextInputEditText titleEt, descriptionInputEt;
    String docID;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        backBtn = findViewById(R.id.img_btn_back_edit);
        saveBtn = findViewById(R.id.img_btn_save_edit);
        titleEt = findViewById(R.id.et_title_edit);
        descriptionInputEt = findViewById(R.id.et_description_edit);

        docID = getIntent().getStringExtra("docID");
        Log.e(TAG, docID+"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        getNoteDetailsByID();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNoteDetails();
                startActivity(new Intent(EditNoteActivity.this, SavedNoteActivity.class));
            }
        });


    }

    public void getNoteDetailsByID(){

        db.collection("notes").document(user.getUid())
                .collection("myNotes").document(docID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snapshot = task.getResult();
                        titleEt.setText(snapshot.get("title").toString());
                        descriptionInputEt.setText(snapshot.get("description").toString());
                    }
                });

    }

    public void updateNoteDetails(){
        String title = titleEt.getText().toString();
        String desc = descriptionInputEt.getText().toString();

        DocumentReference reference = db.collection("notes")
                .document(user.getUid()).collection("myNotes").document(docID);

        Map<String, Object> updatedNote = new HashMap<>();
        updatedNote.put("title", title);
        updatedNote.put("description", desc);

        reference.set(updatedNote).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditNoteActivity.this, "The note is updated successfully.", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditNoteActivity.this, "The note couldn't be updated..", Toast.LENGTH_SHORT).show();

            }
        });

    }
}