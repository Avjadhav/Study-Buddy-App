package com.example.studybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Calculator extends AppCompatActivity {

    EditText Et_cc;
    TextView Tv_cc;
    Button btn_cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        Et_cc = findViewById(R.id.sem1);
        Tv_cc = findViewById(R.id.tv_sem1);
        btn_cc = findViewById(R.id.btn_cc);

        btn_cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal();
            }
        });
    }

    private void cal() {
        float percentage = 0;
        String pt = Et_cc.getText().toString();
        float pointer = Float.parseFloat(pt);

        if(pointer < 7){
            percentage = (float) ((pointer * 7.2) +12);
            Tv_cc.setText(percentage+" %");
        }
        else {
            percentage = (float) ((pointer * 7.4) +12);
            Tv_cc.setText(percentage+" %");
        }
    }
}