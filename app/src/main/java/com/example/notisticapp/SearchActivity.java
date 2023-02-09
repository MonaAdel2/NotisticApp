package com.example.notisticapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    public static final String TAG = "SearchActivity";
    RecyclerView searchRecycler;
    EditText searchEt;

    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseUser user;

    ArrayList<NoteModel> searchArrayList, allNotesArrayList;
    NotesRecyclerAdapter searchRecyclerAdapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEt = findViewById(R.id.et_search);
        searchRecycler = findViewById(R.id.recycler_search);

        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        searchArrayList = new ArrayList<>();
        allNotesArrayList = new ArrayList<>();

        searchRecyclerAdapter = new NotesRecyclerAdapter(searchArrayList, SearchActivity.this, SearchActivity.this);
        searchRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        searchRecycler.setAdapter(searchRecyclerAdapter);



        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().isEmpty()){
                    allNotesArrayList.clear();
                    searchArrayList.clear();
                    searchRecyclerAdapter.notifyDataSetChanged();
                }
                searchNotes(s.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }

    private void searchNotes(String word) {
        allNotesArrayList.clear();
        searchArrayList.clear();


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
                                allNotesArrayList.add(noteModel);
                            }

                        }
                        for (NoteModel note: allNotesArrayList) {
                            if(note.getTitle().toLowerCase().contains(word)){
                                searchArrayList.add(note);
                            }
                        }
                        searchRecyclerAdapter.notifyDataSetChanged();

                    }
                });
    }
}