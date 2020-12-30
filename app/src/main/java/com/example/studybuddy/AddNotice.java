package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddNotice extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText Etnotice;
    Button btn_sendnt,backbtn;
    Spinner spinner_addnt;
    String[] Year ={"Choose Year","FE-A","FE-B","SE-A","SE-B","TE-A","TE-B","BE-A","BE-B"};


    FirebaseAuth auth;
    FirebaseUser user;
    String Userid;
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        spinner_addnt = findViewById(R.id.spinner_addnotice);
        Etnotice = findViewById(R.id.et_addnt);
        btn_sendnt = findViewById(R.id.btn_sendnt);
        backbtn = findViewById(R.id.backbtn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Userid = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("Users").document(Userid);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Notice");



        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Year);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_addnt.setAdapter(arrayAdapter);

        spinner_addnt.setOnItemSelectedListener(this);
        btn_sendnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotice();
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNotice.this , NoticePage.class));
                finish();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void sendNotice() {


        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                           String NameResult = task.getResult().getString("Name");
                            String nt_text = Etnotice.getText().toString();
                            String year = spinner_addnt.getSelectedItem().toString();

                            Notice notice = new Notice(nt_text, Userid , NameResult, year);

                            if(year.equals("Choose Year")){
                                Toast.makeText(AddNotice.this, "Select year", Toast.LENGTH_SHORT).show();
                            }
                            else if(nt_text.isEmpty()){
                                Toast.makeText(AddNotice.this, "Enter Notice", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                databaseReference.push().setValue(notice)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AddNotice.this, "Notice send Sucessfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(AddNotice.this, NoticePage.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(AddNotice.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

//
                            }

                        }
                        else {
                            Toast.makeText(AddNotice.this, ""+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Please select year", Toast.LENGTH_SHORT).show();

    }
}