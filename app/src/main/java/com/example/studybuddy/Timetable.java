package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Timetable extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Timetable";
    LinearLayout addtt;
    ImageButton imageButton;
    ImageView imageView;
    Button btn_upload_tt, backbtn;
    TextView tv_uploadedby;
    String select;
    Bitmap bmp;
    private static final int PICK_IMAGE =1;
    private static final int TAKE_IMAGE =0;
    Spinner spinner_Tt;
    Uri imageuri;
    String[] Year ={"Choose Year","FE-A","FE-B","SE-A","SE-B","TE-A","TE-B","BE-A","BE-B"};

    StorageReference storageReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    FirebaseFirestore firestore;
    DocumentReference documentReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        addtt = findViewById(R.id.addtimetable);
        spinner_Tt = findViewById(R.id.spiner_timetable);
        imageButton = findViewById(R.id.iv_tt_btn);
        imageView = findViewById(R.id.tt_iv);
        btn_upload_tt = findViewById(R.id.tt_upload);
        backbtn= findViewById(R.id.backbtn);
        tv_uploadedby = findViewById(R.id.tt_uploadedby);

        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
         userId = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        documentReference = firestore.collection("Users").document("/"+userId);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Timetable.this, MainActivity.class));
                finishAffinity();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items={"Camera","Gallery", "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(Timetable.this);
                builder.setTitle("Add Image");

                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (items[i].equals("Camera")) {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, TAKE_IMAGE);

                        } else if (items[i].equals("Gallery")) {

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent,PICK_IMAGE);

                        } else if (items[i].equals("Cancel")) {
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.show();



            }
        });

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Year);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Tt.setAdapter(arrayAdapter);

        spinner_Tt.setOnItemSelectedListener(this);

        btn_upload_tt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTimetable();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadData();

    }

    private void loadData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Loading..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String NameResult = task.getResult().getString("Name");
                            String DesignationResult = task.getResult().getString("Designation");
                            String YearResult = task.getResult().getString("Year");

                            if(DesignationResult.equals("Teacher")){

                                addtt.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();


                            }
                            else {
                                DocumentReference dref = firestore.getInstance().collection("Time Table").document(YearResult);
                                dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            String uri = task.getResult().getString("Time Table Url");
                                            String uploadedBy = task.getResult().getString("UploadedBy");
                                            Picasso.get().load(uri).into(imageView);
                                            tv_uploadedby.setText("Uploaded By "+uploadedBy);
                                            progressDialog.dismiss();
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(Timetable.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
//
                            }


                        }
                        else{
                            Toast.makeText(Timetable.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadTimetable() {

        String year = spinner_Tt.getSelectedItem().toString();

        if(select == null){
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
        }
         else if (year.equals("Choose Year")){
            Toast.makeText(this, "Please select year", Toast.LENGTH_SHORT).show();
        }
         else {
             upload(select, year );
        }
    }

    private void upload(final String select, final String year) {

        if (select.equals("0")) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG , 100 , stream);
            byte[] b =stream.toByteArray();

            final StorageReference mref = storageReference.child("Time table/" + year + "/" + year + ".jpg");
            mref.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                                Timestamp Datetime = new Timestamp(new Date());


                                                Map<String, Object> map = new HashMap<>();
                                                map.put("UploadedBy", NameResult);
                                                map.put("Time Table Year", year);
                                                map.put("Teacher Id", userId);
                                                map.put("Time Table Url", uri.toString());
                                                map.put("Date Time", Datetime);

                                                DocumentReference dref = firestore.getInstance().collection("Time Table").document(year);
                                                dref.set(map)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "onComplete: Time Table Data save");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                            Toast.makeText(Timetable.this, "image Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Timetable.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            final StorageReference mref = storageReference.child("Time table/" + year + "/" + year + ".jpg");
            mref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                                Timestamp Datetime = new Timestamp(new Date());


                                                Map<String, Object> map = new HashMap<>();
                                                map.put("UploadedBy", NameResult);
                                                map.put("Time Table Year", year);
                                                map.put("Teacher Id", userId);
                                                map.put("Time Table Url", uri.toString());
                                                map.put("Date Time", Datetime);

                                                DocumentReference dref = firestore.getInstance().collection("Time Table").document(year);
                                                dref.set(map)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "onComplete: Time Table Data save");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                            Toast.makeText(Timetable.this, "image Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Timetable.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK) {
            if (requestCode == TAKE_IMAGE){
                Bundle bundle = data.getExtras();
                 bmp = (Bitmap) bundle.get("data");
                imageView.setImageBitmap(bmp);
                select = "0" ;

            }
            if (requestCode == PICK_IMAGE ) {

                imageuri = data.getData();
                Picasso.get().load(imageuri).into(imageView);
                select = "1" ;
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Please select year", Toast.LENGTH_SHORT).show();
    }


}