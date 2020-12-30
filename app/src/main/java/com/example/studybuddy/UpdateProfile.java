package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText e1,e2;
    TextView t1,t2;
    Button btn_cancel,btn_save,backbtn;
    Spinner spinner_up;
    String deg ="Teacher";
    String[] Year ={"Choose Year (only for Student*)","FE-A","FE-B","SE-A","SE-B","TE-A","TE-B","BE-A","BE-B"};
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentReference reference;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        e1= findViewById(R.id.et_up_2);
        t1= findViewById(R.id.tv_up_3);
        t2= findViewById(R.id.tv_up_4);
        e2= findViewById(R.id.et_up_5);
        spinner_up= findViewById(R.id.spinner_up);
        btn_cancel = findViewById(R.id.cancel_Up);
        btn_save = findViewById(R.id.Save_Up);
        backbtn = findViewById(R.id.backbtn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String userId = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("Users").document("/"+userId);


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfile.this, MainActivity.class));
                finishAffinity();
            }
        });


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Year);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_up.setAdapter(arrayAdapter);

        spinner_up.setOnItemSelectedListener(this);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfile.this, MainActivity.class));
                finish();

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               updateProfile();
            }
        });


    }




    @Override
    protected void onStart() {
        super.onStart();
        getdata();
    }

    private void getdata() {
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.getResult().exists()){
                            String NameResult = task.getResult().getString("Name");
                            String EmailResult = task.getResult().getString("Email");
                            String DesignationResult = task.getResult().getString("Designation");
                            String CollegeResult = task.getResult().getString("College");
                            String YearResult = task.getResult().getString("Year");

                            e1.setText(NameResult);
                            t1.setText(EmailResult);
                            t2.setText(DesignationResult);
                            e2.setText(CollegeResult);

                            if(DesignationResult.equals(deg)){
                                spinner_up.setVisibility(View.GONE);
                            }
                        }
                        else {
                            Toast.makeText(UpdateProfile.this, ""+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateProfile() {

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            String getDes = task.getResult().getString("Designation");
                            String fullname = e1.getText().toString();
                            String College = e2.getText().toString();
                            String year = spinner_up.getSelectedItem().toString();

                            String yr ="Choose Year (only for Student*)";

                            if(getDes.equals(deg)){
                                update(fullname , College , "All");
                            }
                            else {
                                if(year.equals(yr)){
                                    Toast.makeText(UpdateProfile.this, "Please select year", Toast.LENGTH_SHORT).show();
                                }else {
                                    update(fullname , College , year);
                                }
                            }

                        }
                        else {
                            Toast.makeText(UpdateProfile.this, ""+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void update(String fullname, String college, String year) {
        Map<String, Object> map = new HashMap<>();
        map.put("Name", fullname);
        map.put("Year",year);
        map.put("College", college);

        reference.update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(UpdateProfile.this, MainActivity.class));
                            finish();
                            Toast.makeText(UpdateProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(UpdateProfile.this, ""+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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