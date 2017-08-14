package com.example.sarvesh.mycaloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TotalIntakeView extends AppCompatActivity {

    public TextView brkfast;
    public TextView lunch;
    public TextView snacks;
    public TextView dinner;
    private Button btnaddFood;
    private Button btnSubmit;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_intake_view);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnaddFood = (Button) findViewById(R.id.btnAddFood);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setVisibility(LinearLayout.GONE);

        btnaddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(TotalIntakeView.this, AddIntake.class);
                startActivity(myIntent);
            }
        });



    }
}
