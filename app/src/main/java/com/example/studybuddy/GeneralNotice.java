package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GeneralNotice extends AppCompatActivity {
    ConstraintLayout generalLL;
    ImageButton send_btn;
    Button backbtn;
    RecyclerView recycler;
    EditText general_nt;
    GeneralNoticeAdapter generalNoticeAdapter;


    FirebaseAuth auth;
    FirebaseUser user;
    String Userid;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_notice);

        generalLL = findViewById(R.id.generalLL);
        recycler = findViewById(R.id.recyclerview_general);
        general_nt = findViewById(R.id.et_general_nt);
        send_btn = findViewById(R.id.send_general_nt);
        backbtn = findViewById(R.id.backbtn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Userid = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("Users").document(Userid);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("General Notice");

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GeneralNotice.this, MainActivity.class));
                finishAffinity();
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGeneralNotice();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String designationResult = task.getResult().getString("Designation");
                            if(designationResult.equals("Teacher")){
                                generalLL.setVisibility(View.VISIBLE);
                            }
                            initRecycleview();
                        }
                        else {
                            Toast.makeText(GeneralNotice.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        generalNoticeAdapter.stopListening();
    }

    private void initRecycleview() {
        Query query = FirebaseDatabase.getInstance().getReference().child("General Notice");
        FirebaseRecyclerOptions<Notice> options =
                new FirebaseRecyclerOptions.Builder<Notice>()
                        .setQuery(query,Notice.class)
                        .build();
        generalNoticeAdapter = new GeneralNoticeAdapter(options);
        recycler.setAdapter(generalNoticeAdapter);
        generalNoticeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recycler.smoothScrollToPosition(positionStart);
            }
        });
        generalNoticeAdapter.startListening();
    }

    private void sendGeneralNotice() {
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String NameResult = task.getResult().getString("Name");
                            String nt_text = general_nt.getText().toString().trim();


                            Notice notice = new Notice(nt_text, Userid , NameResult);

                           if(nt_text.isEmpty()){
                                Toast.makeText(GeneralNotice.this, "Enter Message", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                databaseReference.push().setValue(notice)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                   general_nt.setText(null);
                                                } else {
                                                    Toast.makeText(GeneralNotice.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

//
                            }

                        }
                        else {
                            Toast.makeText(GeneralNotice.this, ""+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}