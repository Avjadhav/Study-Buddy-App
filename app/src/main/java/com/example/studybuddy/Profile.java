package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends Fragment {

    TextView t1,t2,t3,t4,t5;
    Button upbtn, logout;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentReference reference;
    FirebaseUser user;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        t1= getActivity().findViewById(R.id.tv_prof_2);
        t2= getActivity().findViewById(R.id.tv_prof_3);
        t3= getActivity().findViewById(R.id.tv_prof_4);
        t4= getActivity().findViewById(R.id.tv_prof_5);
        t5= getActivity().findViewById(R.id.tv_prof_6);
        upbtn = getActivity().findViewById(R.id.btn_edit_profile);
        logout = getActivity().findViewById(R.id.logout);

        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateProfile.class));

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getActivity(),Login.class));
                getActivity().finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String userId = user.getUid();
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("Users").document("/"+userId);



    }

    @Override
    public void onStart() {
        super.onStart();

        getdata();
    }

    private void getdata() {
        final String deg = "Teacher";
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

                            t1.setText(NameResult);
                            t2.setText(EmailResult);
                            t3.setText(DesignationResult);
                            t4.setText(CollegeResult);
                            t5.setText(YearResult);

                            if(deg.equals(DesignationResult)){
                                t5.setVisibility(View.GONE);
                            }
                        }
                        else {
                            Toast.makeText(getActivity(), ""+task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

