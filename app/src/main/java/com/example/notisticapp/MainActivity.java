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
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
    public static final String SHARED_PREFS = "sharedPrefs";

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
//        notesRecycler.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
        notesRecyclerAdapter = new NotesRecyclerAdapter(notesArrayList, MainActivity.this, MainActivity.this);
        notesRecycler.setAdapter(notesRecyclerAdapter);

        eventChangeListener();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();

                // remove sharedpreferences
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user", "false");
                editor.apply();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });


    }

    private void eventChangeListener() {
        //orderBy("title", Query.Direction.ASCENDING)

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

    @Override
    protected void onRestart() {
        super.onRestart();
        notesRecyclerAdapter.notifyDataSetChanged();
    }


}

