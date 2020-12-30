package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DocumentPage extends AppCompatActivity {
    private static final String TAG ="DocumentPage" ;
    FloatingActionButton toadddoc;
    RecyclerView recycler;
    Button backbtn;

    DocumentAdapter documentAdapter;


    FirebaseAuth auth;
    FirebaseUser user;
    String Userid;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_page);

        toadddoc =findViewById(R.id.floatingActionButton_notes);
        recycler = findViewById(R.id.recyclerview_notes);
        backbtn= findViewById(R.id.backbtn);

        toadddoc.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Userid = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("Users").document(Userid);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DocumentPage.this, MainActivity.class));
                finishAffinity();
            }
        });

        toadddoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DocumentPage.this, AddDocument.class));
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        }
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
                                toadddoc.setVisibility(View.VISIBLE);
                                initRecycleview(yearResult, auth.getCurrentUser());
                            }
                            else{
                                initRecycleview(yearResult, auth.getCurrentUser());
                            }
                        }
                        else {
                            Toast.makeText(DocumentPage.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        documentAdapter.stopListening();
    }

    private void initRecycleview(String yearResult, FirebaseUser currentUser) {

        if (yearResult.equals("All")){
            Query query = FirebaseDatabase.getInstance().getReference().child("Documents").orderByChild("teacher_id").equalTo(currentUser.getUid());
            FirebaseRecyclerOptions<Document> options =
                    new FirebaseRecyclerOptions.Builder<Document>()
                            .setQuery(query,Document.class)
                            .build();
            documentAdapter = new DocumentAdapter(options);
            recycler.setAdapter(documentAdapter);
            documentAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    recycler.smoothScrollToPosition(positionStart);
                }
            });
            documentAdapter.startListening();

        }else {
            Query query = FirebaseDatabase.getInstance().getReference().child("Documents").orderByChild("document_year").equalTo(yearResult);
            FirebaseRecyclerOptions<Document> options =
                    new FirebaseRecyclerOptions.Builder<Document>()
                            .setQuery(query,Document.class)
                            .build();

            documentAdapter = new DocumentAdapter(options);

            recycler.setAdapter(documentAdapter);
            documentAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    recycler.smoothScrollToPosition(positionStart);
                }
            });
            documentAdapter.startListening();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "external public folder access granted");
                } else {
                    Log.e(TAG, "external public folder access denied sending user to main screen");
                    Toast.makeText(this,
                            "Please grant permission to access public folders to use the feature",
                            Toast.LENGTH_SHORT).show();
                    toMainScreen();
                }
                return;
            }
        }
    }

    private void toMainScreen() {
       startActivity(new Intent(DocumentPage.this, MainActivity.class));
       finish();
    }
}