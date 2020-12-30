package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText emailforget;
    Button btnfgpwd;
    String email;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailforget = findViewById(R.id.et_fgpwd);
        btnfgpwd = findViewById(R.id.btn_fgpwd);

        auth = FirebaseAuth.getInstance();

        email =getIntent().getStringExtra("Emailtoforpwd");
         emailforget.setText(email);

         btnfgpwd.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 email = emailforget.getText().toString().trim();

                 final AlertDialog alertDialog = new AlertDialog.Builder(ForgotPassword.this).create();
                 alertDialog.setTitle("Reset Password");
                 alertDialog.setMessage("Password reset email has been sent..! ");
                 alertDialog.setCanceledOnTouchOutside(false);
                 alertDialog.setIcon(R.drawable.ic_baseline_email_24);
                 alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                         startActivity(new Intent(ForgotPassword.this, Login.class));
                         finish();
                     }
                 });

                 auth.sendPasswordResetEmail(email)
                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()) {

                                     alertDialog.show();
                                 }
                                 else {
                                     Toast.makeText(ForgotPassword.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });

             }
         });

    }
}