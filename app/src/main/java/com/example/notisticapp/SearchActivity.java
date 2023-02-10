package com.example.notisticapp;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    ArrayList<NoteModel> searchArrayList2, allNotesArrayList;
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

        searchArrayList2 = new ArrayList<>();
        allNotesArrayList = new ArrayList<>();

        searchRecyclerAdapter = new NotesRecyclerAdapter(searchArrayList2, SearchActivity.this, SearchActivity.this);
        searchRecycler.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        searchRecycler.setAdapter(searchRecyclerAdapter);



        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                searchNotes(s.toString().toLowerCase());

                search2(s.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }

    private void searchNotes(String word) {
        allNotesArrayList.clear();
        searchArrayList2.clear();

//        .orderBy("title", Query.Direction.ASCENDING)

        db.collection("notes").document(user.getUid())
                .collection("myNotes")
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
                                searchArrayList2.add(note);
                            }
                        }
                        searchRecyclerAdapter.notifyDataSetChanged();

                    }
                });
    }

    public  void search2(String word){
        searchArrayList2.clear();

        db.collection("notes").document(user.getUid())
                .collection("myNotes")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                            NoteModel noteModel = new NoteModel(document.getString("title"), document.getString("description"));
                            if(noteModel.getTitle().toLowerCase().contains(word)){
                                searchArrayList2.add(noteModel);
                            }

                        }
                        searchRecyclerAdapter.notifyDataSetChanged();

                    }

                });


    }
}