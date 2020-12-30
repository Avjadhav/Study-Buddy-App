package com.example.studybuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Dashboard extends Fragment {
    ConstraintLayout Notice_ly,Timetable,calculator,notes;
    FloatingActionButton general_nt;
    FirebaseUser user;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    DocumentReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Notice_ly = getActivity().findViewById(R.id.noticely);
        Timetable = getActivity().findViewById(R.id.timetable);
        calculator = getActivity().findViewById(R.id.cgpi_cc);
        notes = getActivity().findViewById(R.id.notes);
        general_nt = getActivity().findViewById(R.id.general_notice);

        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        String userid= user.getUid();
        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("Users").document(userid);

        Notice_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity() ,NoticePage.class));
            }
        });


        Timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity() ,Timetable.class));
            }
        });

        calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity() ,Calculator.class));
            }
        });

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DocumentPage.class));
            }
        });


        general_nt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), GeneralNotice.class));
            }
        });

    }


}
