package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddDocument extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "AddDocument";
    ImageButton imageButton;
    TextView namedoc;

    Button upload,backbtn;

    Spinner spinner_adddocument;
    String[] Year ={"Choose Year","FE-A","FE-B","SE-A","SE-B","TE-A","TE-B","BE-A","BE-B"};

    private static final int PICK_FILE =10;
    Uri document;
    String nameofdoc;
    FirebaseAuth auth;
    FirebaseUser user;
    String Userid;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_document);

        upload = findViewById(R.id.btn_senddoc);

        namedoc = findViewById(R.id.docname);
        spinner_adddocument = findViewById(R.id.spinner_adddoc);
        imageButton = findViewById(R.id.adddocbtn);
        backbtn = findViewById(R.id.backbtn);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        Userid = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Documents");
        documentReference = FirebaseFirestore.getInstance().collection("Users").document("/"+Userid);


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Year);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_adddocument.setAdapter(arrayAdapter);

        spinner_adddocument.setOnItemSelectedListener(this);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploaddoc();
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddDocument.this, DocumentPage.class));
                finish();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("application/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_FILE);
            }
        });
    }

    private void uploaddoc() {
        String year = spinner_adddocument.getSelectedItem().toString();

        if(document == null){
            Toast.makeText(this, "Please select document", Toast.LENGTH_SHORT).show();
        }
        else if (year.equals("Choose Year")){
            Toast.makeText(this, "Please select year", Toast.LENGTH_SHORT).show();
        }
        else {
            uploading(document, year );
        }
    }

    private void uploading(Uri document, final String year) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        final StorageReference mref = storageReference.child("Document/" + year + "/" + nameofdoc);
        mref.putFile(document).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setProgress((int) progress);

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        documentReference.get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.getResult().exists()) {
                                            String NameResult = task.getResult().getString("Name");

                                            Document doc = new Document(NameResult,year,Userid,uri.toString(),nameofdoc);


                                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Documents");
                                            databaseReference.push().setValue(doc)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "onComplete: Document Data save");
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                        progressDialog.dismiss();
                        Toast.makeText(AddDocument.this, "Document Uploaded", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddDocument.this, DocumentPage.class));
                        finish();
                    }
                });
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK){
            if(requestCode == PICK_FILE){
                document = data.getData();
                String uriString =document.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(document, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            nameofdoc = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            namedoc.setText(nameofdoc);
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    nameofdoc = myFile.getName();
                    namedoc.setText(nameofdoc);
                }

            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}