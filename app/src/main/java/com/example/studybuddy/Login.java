package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextView gotosignup,forgotpass;
    EditText Email,Pass;
    Button login_btn;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gotosignup =  findViewById(R.id.tv_login_4);
        Email = findViewById(R.id.et_email_log);
        Pass = findViewById(R.id.et_pass_log);
        login_btn = findViewById(R.id.btn_login);
        forgotpass = findViewById(R.id.tv_login_3);


        auth = FirebaseAuth.getInstance();

        gotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =  Email.getText().toString();
                String pass = Pass.getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)){

                    auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                startActivity(new Intent(Login.this, MainActivity.class));
                                Toast.makeText(Login.this, "Welcome", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{

                                Toast.makeText(Login.this, "Error :"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }else {
                    Toast.makeText(Login.this, "Please Fill all Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= Email.getText().toString();
                    Intent intent = new Intent(Login.this, ForgotPassword.class);
                    intent.putExtra("Emailtoforpwd",email );
                    startActivity(intent);

            }
        });

    }

}