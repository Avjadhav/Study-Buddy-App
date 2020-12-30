package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

public class NoticePage extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    RecyclerView recycler;
    Button backbtn;
    NoticeAdapter noticeAdapter;


    FirebaseAuth auth;
    FirebaseUser user;
    String Userid;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_page);


        floatingActionButton = findViewById(R.id.floatingActionButton);
        recycler = findViewById(R.id.recyclerview);
        backbtn= findViewById(R.id.backbtn);

        floatingActionButton.setVisibility(View.GONE);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoticePage.this, AddNotice.class));
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoticePage.this, MainActivity.class));
                finishAffinity();
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Userid = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("Users").document(Userid);
        databaseReference = FirebaseDatabase.getInstance().getReference();



    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String usid = task.getResult().getString("User Id");
                            String yearResult = task.getResult().getString("Year");
                            String designationResult = task.getResult().getString("Designation");
                            if(designationResult.equals("Teacher")){
                                floatingActionButton.setVisibility(View.VISIBLE);
                                initRecycleview(yearResult, auth.getCurrentUser());
                            }
                            else{
                                initRecycleview(yearResult, auth.getCurrentUser());
                            }
                        }
                        else {
                            Toast.makeText(NoticePage.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }

    @Override
    protected void onStop() {
        super.onStop();
        noticeAdapter.stopListening();
    }

    private void initRecycleview(String yearResult, FirebaseUser user){

        if (yearResult.equals("All")){
            Query query = FirebaseDatabase.getInstance().getReference().child("Notice").orderByChild("userId").equalTo(user.getUid());
            FirebaseRecyclerOptions<Notice> options =
                    new FirebaseRecyclerOptions.Builder<Notice>()
                            .setQuery(query,Notice.class)
                            .build();
            noticeAdapter = new NoticeAdapter(options);
            recycler.setAdapter(noticeAdapter);
            noticeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    recycler.smoothScrollToPosition(positionStart);
                }
            });
            noticeAdapter.startListening();

        }else {
            Query query = FirebaseDatabase.getInstance().getReference().child("Notice").orderByChild("year").equalTo(yearResult);
            FirebaseRecyclerOptions<Notice> options =
                    new FirebaseRecyclerOptions.Builder<Notice>()
                            .setQuery(query,Notice.class)
                            .build();

            noticeAdapter = new NoticeAdapter(options);

            recycler.setAdapter(noticeAdapter);
           noticeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
               @Override
               public void onItemRangeInserted(int positionStart, int itemCount) {
                   super.onItemRangeInserted(positionStart, itemCount);
                   recycler.smoothScrollToPosition(positionStart);
               }
           });
            noticeAdapter.startListening();


        }
    }
}