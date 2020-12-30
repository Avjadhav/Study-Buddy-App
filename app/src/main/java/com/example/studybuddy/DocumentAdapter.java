package com.example.studybuddy;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DocumentAdapter extends FirebaseRecyclerAdapter<Document , DocumentAdapter.DocumentHolder> {

    private static final String TAG = "DocumentAdapter";
    private String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory
            (Environment.DIRECTORY_DOWNLOADS).getPath();
    Context context;


    ProgressDialog progressDialog;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    FirebaseUser user;
    String Userid;


    public DocumentAdapter(@NonNull FirebaseRecyclerOptions<Document> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final DocumentHolder holder, final int position, @NonNull final Document document) {
        holder.teachername.setText(document.getUploadedBy());
        holder.filename.setText(document.getFilename());
        String date = new SimpleDateFormat("EE dd-MM-yy hh:mm a").format(new Date(document.getDate_Time()*1000));
        holder.time.setText(date);


       holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                downloadFile(document.getDocument_Url(), document.getFilename());}


        });

       holder.delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               deleteFile(document.getDocument_year(), document.getFilename());
           }
       });

    }

    private void deleteFile(String document_year, final String filename) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Deleting..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("File Deleted..");
        alertDialog.setMessage("Document Deleted Successfully..");
        alertDialog.setIcon(R.drawable.ic_baseline_delete_forever_24);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference st = storageRef.child("Document/"+document_year+"/"+filename);
        st.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference();
                       Query dt =databaseReference.child("Documents").orderByChild("filename").equalTo(filename);
                    dt.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1: snapshot.getChildren()){
                                 snapshot1.getRef().removeValue();
                                 progressDialog.dismiss();
                                 alertDialog.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAG, "onCancelled: ",error.toException());
                        }
                    });
                    Log.d(TAG, "onComplete: Deleted");
                }else {
                    Log.d(TAG, "onComplete: Failed");
                }
            }
        });
    }

    private void downloadFile(String document_url, String filename) {

        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(document_url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(filename);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDescription("Downloading");
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Study Buddy/"  +filename);

        downloadmanager.enqueue(request);

    }


    @NonNull
    @Override
    public DocumentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.document_row, parent, false);
        context = parent.getContext();
        return new DocumentHolder(view);
    }

    class DocumentHolder extends RecyclerView.ViewHolder{
        TextView teachername,filename,time;
        Button download,delete;

        public DocumentHolder(@NonNull View itemView) {
            super(itemView);
            teachername = itemView.findViewById(R.id.list_tv_1);
            filename = itemView.findViewById(R.id.list_tv_2);
            time = itemView.findViewById(R.id.list_tv_3);
            download = itemView.findViewById(R.id.documentdownload_btn);
            delete = itemView.findViewById(R.id.documentdelete_btn);

            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            Userid = user.getUid();

            firestore.collection("Users").document(Userid).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                String Designation = task.getResult().getString("Designation");

                                if(Designation.equals("Teacher")){
                                    delete.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });


        }
    }

}
