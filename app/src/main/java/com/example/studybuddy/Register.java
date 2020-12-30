package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText Name,Email,Password,et_key;
    TextView gotologin;
    CheckBox checkBox;
    String Designation = "Student";
    String Defaultkey ="1234";
    Spinner spinner_reg;
    String[] Year ={"Choose Year (only for Student*)","FE-A","FE-B","SE-A","SE-B","TE-A","TE-B","BE-A","BE-B"};

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentReference reference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Name = findViewById(R.id.et_name);
        Email = findViewById(R.id.et_email);
        Password = findViewById(R.id.et_pass);
        et_key = findViewById(R.id.et_key);
        spinner_reg = findViewById(R.id.Spinner_reg);
        checkBox =  findViewById(R.id.checkbox_reg);
        gotologin = findViewById(R.id.tv_reg_3);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Year);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_reg.setAdapter(arrayAdapter);

        spinner_reg.setOnItemSelectedListener(this);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    et_key.setVisibility(View.VISIBLE);
                    Designation ="Teacher";
                    spinner_reg.setVisibility(View.GONE);
                }
                else {
                    et_key.setVisibility(View.GONE);
                    Designation ="Student";
                    spinner_reg.setVisibility(View.VISIBLE);
                }
            }
        });

        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });



    }

    public void RegisterUser(View view) {
        try {
            String fullname = Name.getText().toString();
            String email = Email.getText().toString();
            String pass = Password.getText().toString();
            String year = spinner_reg.getSelectedItem().toString();
            String skey = et_key.getText().toString();

            if (fullname.isEmpty()) {
                Name.setError("Enter a Full Name");
            }
            else if (pass.isEmpty()) {
                Password.setError("Enter a Password");
            }
            else if (email.isEmpty()) {
                Email.setError("Enter Valid Email");
            }



            if (Designation == "Teacher") {
                if (skey.equals(Defaultkey)) {
                    register(fullname, email, pass, Designation, "All");
                } else {
                    et_key.setError("Invalid Key");
                }
            } else {
                if(year == "Choose Year (only for Student*)"){
                    Toast.makeText(this, "Please select year", Toast.LENGTH_SHORT).show();
                }else {
                    register(fullname, email, pass, Designation, year);
                }
            }
        }catch (Exception e){

        }
    }

    private void register(final String fullname, final String email, String pass, final String designation, final String year) {

            auth.createUserWithEmailAndPassword(email,pass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = user.getUid();
                            reference = firestore.collection("Users").document("/" + userId);

                            Map<String, Object> map = new HashMap<>();
                            map.put("Name", fullname);
                            map.put("Email", email);
                            map.put("Year",year);
                            map.put("Designation", designation);
                            map.put("UserId", userId);

                            reference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Registration Succesfully Done..", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Register.this, Login.class));
                                        finish();
                                    } else {
                                        Toast.makeText(Register.this, "" + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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