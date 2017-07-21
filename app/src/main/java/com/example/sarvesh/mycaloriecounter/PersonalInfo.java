package com.example.sarvesh.mycaloriecounter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalInfo extends AppCompatActivity {

    private EditText height;
    private EditText weight;
    private EditText age;
    private Button btnSubmit;
    private TextView txtResult;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        height = (EditText) findViewById(R.id.editHeight);
        weight = (EditText) findViewById(R.id.editWeight);
        age = (EditText) findViewById(R.id.editAge);
        txtResult = (TextView) findViewById(R.id.txtResult);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        linearLayout.setVisibility(LinearLayout.GONE);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (height.getText().length()>0 && weight.getText().length()>0 && age.getText().length()>0) {
                    float hgt = Float.parseFloat(height.getText().toString());
                    hgt =  hgt /100;
                    float wgt = Float.parseFloat(weight.getText().toString());
                    int ages = Integer.parseInt(age.getText().toString());
                    if(hgt > 0 && wgt > 0 && ages>0){
                        float bmi = wgt/(hgt * hgt);
                        double bmrDouble = (10 * wgt) +(6.25 * hgt * 100) - (5 * ages) +5;
                        int bmr = (int) bmrDouble;
                        String category=bmiCalculate(bmi);
                        linearLayout.setVisibility(LinearLayout.VISIBLE);
                        txtResult.setText("Your BMI is " + bmi + ". You are " + category + " .Maximum intake of calories daily is " + bmr + " cal. To reduce 0.5kg per week have a daily intake of " + (bmr-500)+ " cal." );
                    }
                    else {
                        if(height.getText().length()<0){
                            Toast.makeText(PersonalInfo.this,"Please enter a valid Height value.",Toast.LENGTH_LONG).show();
                            linearLayout.setVisibility(LinearLayout.GONE);
                            height.setText("");
                            height.requestFocus();
                        }
                        else if(weight.getText().length()<0){
                            Toast.makeText(PersonalInfo.this,"Please enter a valid Weight value.",Toast.LENGTH_LONG).show();
                            linearLayout.setVisibility(LinearLayout.GONE);
                            weight.setText("");
                            weight.requestFocus();
                        }
                        else{
                            Toast.makeText(PersonalInfo.this,"Please enter a valid Age value.",Toast.LENGTH_LONG).show();
                            linearLayout.setVisibility(LinearLayout.GONE);
                            age.setText("");
                            age.requestFocus();
                        }
                    }
                }
                else {
                    Toast.makeText(PersonalInfo.this,"Fill all the fields.",Toast.LENGTH_LONG).show();
                    linearLayout.setVisibility(LinearLayout.GONE);
                }
            }
        });
    }
    public String bmiCalculate(float x){
        String category = "";
        if (x<15){
            category =  "Very severely underweight";
        }
        else if(x>=15 && x<16) {
            category =  "Severely underweight";
        }
        else if(x>=16 && x<18.5){
            category =  "Underweight";
        }
        else if(x>=18.5 && x<25){
            category =  "Normal Healthy weight";
        }
        else if(x>=25 && x<30){
            category =  "Overweight";
        }
        else if(x>=30 && x<35){
            category =  "Moderately Obese";
        }
        else if(x>=35 && x<40){
            category =  "Severely Obese";
        }
        else if(x>=40){
            category =  "Very Severely Obese";
        }
        return (category);
    }
}
