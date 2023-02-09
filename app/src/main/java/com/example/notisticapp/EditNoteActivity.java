package com.example.notisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
    AppCompatButton wSaveBtn, wDiscardBtn;
    TextInputEditText titleEt, descriptionInputEt;
    ProgressBar progressBarSave;
    String docID;
    Dialog warningDialog;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        backBtn = findViewById(R.id.img_btn_back_edit);
        saveBtn = findViewById(R.id.img_btn_save_edit);
        titleEt = findViewById(R.id.et_title_edit);
        descriptionInputEt = findViewById(R.id.et_description_edit);
        progressBarSave = findViewById(R.id.progress_edit);

        docID = getIntent().getStringExtra("docID");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        getNoteDetailsByID();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateNoteDetails();
                startActivity(new Intent(EditNoteActivity.this, MainActivity.class));
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // need to check whether the modified note saved or not ****************
                String newTitle = titleEt.getText().toString().trim();
                String newDesc = descriptionInputEt.getText().toString().trim();

                db.collection("notes").document(user.getUid())
                        .collection("myNotes").document(docID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot snapshot = task.getResult();
                                String oldtitle = snapshot.get("title").toString();
                                String oldDesc = snapshot.get("description").toString();

                                if(!oldtitle.equals(newTitle) || !oldDesc.equals(newDesc)){

                                    warningDialog = new Dialog(EditNoteActivity.this);
                                    warningDialog.setContentView(R.layout.save_dialog_custom);
                                    wDiscardBtn = warningDialog.findViewById(R.id.discard_btn_dialog);
                                    wSaveBtn = warningDialog.findViewById(R.id.save_btn_dialog);
                                    warningDialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                                    warningDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_button));
                                    warningDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    warningDialog.show();
                                    warningDialogDisplay();

                                }
                                else{
                                    finish();
                                    startActivity(new Intent(EditNoteActivity.this, MainActivity.class));
                                }
                            }
                        });

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

        progressBarSave.setVisibility(View.VISIBLE);
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

    private void warningDialogDisplay(){
        wSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningDialog.dismiss();
                updateNoteDetails();
                startActivity(new Intent(EditNoteActivity.this, MainActivity.class));
                finish();
            }
        });

        wDiscardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningDialog.dismiss();
                startActivity(new Intent(EditNoteActivity.this, MainActivity.class));
                finish();
            }
        });

    }
}