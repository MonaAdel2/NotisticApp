package com.example.notisticapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class SavedNoteActivity extends AppCompatActivity {

    public static final String TAG = "SavedNoteActivity";

    ImageButton editBtn, backBtn;
    TextView titleTv, descriptionTv;
    String noteTitle, noteDesc, noteID;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_note);

        editBtn = findViewById(R.id.img_btn_edit_saved);
        backBtn = findViewById(R.id.img_btn_back_saved);
        titleTv = findViewById(R.id.tv_title_saved);
        descriptionTv = findViewById(R.id.tv_description_saved);

        noteID = getIntent().getStringExtra("docID");


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        intent = new Intent(SavedNoteActivity.this, EditNoteActivity.class);
        intent.putExtra("docID", noteID);

        getNoteDetailsByID();
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    public void getNoteDetailsByID(){

         db.collection("notes").document(user.getUid())
                        .collection("myNotes").document(noteID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         DocumentSnapshot snapshot = task.getResult();
                         titleTv.setText(snapshot.get("title").toString());
                         descriptionTv.setText(snapshot.get("description").toString());
                     }
                 });

    }
}