package com.example.notisticapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    AppCompatButton addBtn;
    ImageButton searchBtn, logoutBtn;

    RecyclerView notesRecycler;
    ArrayList<NoteModel> notesArrayList;
    NotesRecyclerAdapter notesRecyclerAdapter;

    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;

    ArrayList<String> currentIDs;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBtn = findViewById(R.id.btn_plus);
        searchBtn = findViewById(R.id.img_btn_search);
        logoutBtn = findViewById(R.id.img_btn_logout);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        notesRecycler = findViewById(R.id.recycler_notes_main);
        notesArrayList = new ArrayList<>();
        currentIDs = new ArrayList<>();


        notesRecycler.setHasFixedSize(true);
        notesRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        notesRecyclerAdapter = new NotesRecyclerAdapter(notesArrayList, MainActivity.this);
        notesRecycler.setAdapter(notesRecyclerAdapter);

        eventChangeListener();

//        notesRecyclerAdapter.setOnClick(new Consumer<NoteModel>() {
//            @Override
//            public void accept(NoteModel noteModel) {
//                Intent intent = new Intent(MainActivity.this , SavedNoteActivity.class);
//                intent.putExtra("noteTitle", noteModel.getTitle()); //************************************
//                intent.putExtra("noteDesc", noteModel.getDescription()); //************************************
////                Log.e(TAG, db.collection("notes").document(user.getUid())
////                        .collection("myNotes").document().getId()+ "**********************************************************************");
//                db.collection("notes").document(user.getUid())
//                        .collection("myNotes").document().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                               String id = documentSnapshot.getId();
//                               intent.putExtra("noteID", id);
//                               Log.e(TAG, id + "//////////////////////////////////////////////////////////////");
//                                startActivity(intent);
//                            }
//                        });
////                startActivity(intent);
//            }
//        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });


    }

    private void eventChangeListener() {
        db.collection("notes").document(user.getUid())
              .collection("myNotes").orderBy("title", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e(TAG, error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        NoteModel noteModel = dc.getDocument().toObject(NoteModel.class);
                        notesArrayList.add(noteModel);
                    }

                    notesRecyclerAdapter.notifyDataSetChanged();

                }

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        notesRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(notesRecyclerAdapter != null){
            notesRecyclerAdapter.notifyDataSetChanged();
        }

    }



}

