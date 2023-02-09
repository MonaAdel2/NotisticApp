package com.example.notisticapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;


public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {
    ArrayList<NoteModel> notesArrayList;
    Context context;
    Activity activity;

    Consumer<NoteModel> onClick;
    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAuth auth;
    String id;

    public void setOnClick(Consumer<NoteModel> onClick){
        this.onClick = onClick;
    }

    public NotesRecyclerAdapter(ArrayList<NoteModel> notesArrayList, Context context) {
        this.notesArrayList= notesArrayList;
        this.context = context;
    }
    public NotesRecyclerAdapter(ArrayList<NoteModel> notesArrayList, Context context, Activity activity) {
        this.notesArrayList= notesArrayList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        int colorCode = GetRandomColor();
        holder.note.setBackgroundColor(holder.itemView.getResources().getColor(colorCode, null));
        holder.noteTitle.setText(notesArrayList.get(position).getTitle());
        holder.noteDesc.setText(notesArrayList.get(position).getDescription());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View v) {
//                // open the note clicked
//                if(onClick != null){
//                    onClick.accept(notesArrayList.get(position));
//                }
//                Intent intent =  new Intent(v.getContext(), SavedNoteActivity.class);
//                v.getContext().startActivity(intent);
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                auth = FirebaseAuth.getInstance();
                user = FirebaseAuth.getInstance().getCurrentUser();
                db.collection("notes").document(user.getUid())
                        .collection("myNotes")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                String docId = queryDocumentSnapshots.getDocuments().get(position).getId();
                                id = docId;
                                Intent intent =  new Intent(v.getContext(), SavedNoteActivity.class);
                                intent.putExtra("docID", docId);
                                v.getContext().startActivity(intent);
                            }
                        });

            }
        });
        holder.menuImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.setGravity(Gravity.END);
                popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        db = FirebaseFirestore.getInstance();
                        auth = FirebaseAuth.getInstance();
                        user = FirebaseAuth.getInstance().getCurrentUser();

                        db.collection("notes").document(user.getUid())
                                .collection("myNotes")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                                        String docId = queryDocumentSnapshots.getDocuments().get(position).getId();
                                        Intent intent =  new Intent(v.getContext(), EditNoteActivity.class);
                                        intent.putExtra("docID",docId);
                                        v.getContext().startActivity(intent);
                                    }
                                });


                        return false;
                    }
                });

                popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        db = FirebaseFirestore.getInstance();
                        auth = FirebaseAuth.getInstance();
                        user = FirebaseAuth.getInstance().getCurrentUser();

                        db.collection("notes").document(user.getUid())
                                .collection("myNotes")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot queryDocumentSnapshots = task.getResult();
                                        String docId = queryDocumentSnapshots.getDocuments().get(position).getId();

                                        DeleteNote(docId, position);

                                        Intent intent = new Intent();
                                        intent.setClass(activity, activity.getClass());
                                        activity.finish();
                                        activity.startActivity(intent);

                                    }
                                });



                        return false;
                    }
                });

                popupMenu.show();
            }
        });


    }

    private int GetRandomColor() {
        List<Integer> allColors = new ArrayList<>();
        allColors.add(R.color.baby_blue);
        allColors.add(R.color.baby_pink);
        allColors.add(R.color.baby_purple);
        allColors.add(R.color.baby_green);
        allColors.add(R.color.baby_yellow);
        allColors.add(R.color.baby_orange);

        allColors.add(R.color.isabelline);
        allColors.add(R.color.thistle);
        allColors.add(R.color.antique_white);
        allColors.add(R.color.columbia_blue);
        allColors.add(R.color.baby_blue2);
        allColors.add(R.color.egg_shell);
        allColors.add(R.color.lavender_gray);
        allColors.add(R.color.opal);



        Random randomColor =  new Random();
        int position = randomColor.nextInt(allColors.size());
        return allColors.get(position);

    }

    private void DeleteNote(String docID, int pos){


        DocumentReference document = db.collection("notes").document(user.getUid())
                .collection("myNotes")
                .document(docID);
        document.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "The note is deleted successfully.", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to delete the note, try again.", Toast.LENGTH_SHORT).show();
            }
        });



    }

    public static void restartActivity(Activity act){

        Intent intent=new Intent();
        intent.setClass(act, act.getClass());
        act.finish();
        act.startActivity(intent);


    }
    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteDesc;
        LinearLayout note;
        ImageView menuImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.tv_title_card);
            noteDesc = itemView.findViewById(R.id.tv_discription_card);
            note = itemView.findViewById(R.id.note_layout_card);
            menuImg = itemView.findViewById(R.id.img_more_options_card);
        }
    }
}
