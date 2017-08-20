package com.example.sarvesh.mycaloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.hasura.sdk.Callback;
import io.hasura.sdk.Hasura;
import io.hasura.sdk.HasuraClient;
import io.hasura.sdk.HasuraUser;
import io.hasura.sdk.ProjectConfig;
import io.hasura.sdk.exception.HasuraException;
import io.hasura.sdk.exception.HasuraInitException;


public class PersonalInfo extends AppCompatActivity implements OnItemSelectedListener {

    private EditText height;
    private EditText weight;
    private EditText age;
    private Button btnSubmit;
    private TextView txtResult;
    private ImageView imgIntake;
    HasuraUser user;
    HasuraClient client;
    private LinearLayout linearLayout;
    ArrayList<Integer> userid = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        height = (EditText) findViewById(R.id.editHeight);
        weight = (EditText) findViewById(R.id.editWeight);
        age = (EditText) findViewById(R.id.editAge);
        txtResult = (TextView) findViewById(R.id.txtResult);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        imgIntake = (ImageView) findViewById(R.id.imgIntake);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setVisibility(LinearLayout.GONE);

        try {
            Hasura.setProjectConfig(new ProjectConfig.Builder()
                    .setCustomBaseDomain("becalmed98.hasura-app.io")
//                    .enableOverHttp()
                    .build())
                    .initialise(this);
        } catch (HasuraInitException e) {
            e.printStackTrace();
        }
        client = Hasura.getClient();
        user = client.getUser();

        // Spinner element
        final Spinner spinner1 = (Spinner) findViewById(R.id.category);
        final Spinner spinner2 = (Spinner) findViewById(R.id.criteria);

        // Spinner click listener
        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);

        // Spinner Category Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Select Category");
        categories.add("Sedentary");
        categories.add("Lightly Active");
        categories.add("Moderately Active");
        categories.add("Very Active");
        categories.add("Extra Active");

        //Spinner Criteria Drop Down elements
        final List<String> weightcriteria = new ArrayList<String>();
        weightcriteria.add("Select Criteria");
        weightcriteria.add("Reduce 0.5kg per week");
        weightcriteria.add("Maintain Weight");
        weightcriteria.add("Increase 0.5kg per week");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weightcriteria);

        // Drop down layout style - list view with radio button
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner1.setAdapter(dataAdapter1);
        spinner2.setAdapter(dataAdapter2);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (height.getText().length() == 0) {
                    Toast.makeText(PersonalInfo.this, "Please enter a valid Height value.", Toast.LENGTH_LONG).show();
                    linearLayout.setVisibility(LinearLayout.GONE);
                    height.setText("");
                    height.requestFocus();
                    return;
                }
                if (weight.getText().length() == 0) {
                    Toast.makeText(PersonalInfo.this, "Please enter a valid Weight value.", Toast.LENGTH_LONG).show();
                    linearLayout.setVisibility(LinearLayout.GONE);
                    weight.setText("");
                    weight.requestFocus();
                    return;
                }
                if (age.getText().length() == 0) {
                    Toast.makeText(PersonalInfo.this, "Please enter a valid Age value.", Toast.LENGTH_LONG).show();
                    linearLayout.setVisibility(LinearLayout.GONE);
                    age.setText("");
                    age.requestFocus();
                    return;
                }
                final String text = spinner1.getSelectedItem().toString();
                final String weightcrt = spinner2.getSelectedItem().toString();
                if (text.equals("Select Category")) {
                    Toast.makeText(PersonalInfo.this, "Please select a category", Toast.LENGTH_LONG).show();
                    linearLayout.setVisibility(LinearLayout.GONE);
                    return;
                }
                if (weightcrt.equals("Select Criteria")) {
                    Toast.makeText(PersonalInfo.this, "Please select a diet criteria", Toast.LENGTH_LONG).show();
                    linearLayout.setVisibility(LinearLayout.GONE);
                    return;
                }
                float hgt = Float.parseFloat(height.getText().toString());
                float wgt = Float.parseFloat(weight.getText().toString());
                int ages = Integer.parseInt(age.getText().toString());
                if (hgt > 240) {
                    Toast.makeText(PersonalInfo.this, "Please enter a valid Height.", Toast.LENGTH_LONG).show();
                    linearLayout.setVisibility(LinearLayout.GONE);
                    height.setText("");
                    height.requestFocus();
                    return;
                }
                if (wgt > 300) {
                    Toast.makeText(PersonalInfo.this, "Please enter a valid Weight.", Toast.LENGTH_LONG).show();
                    linearLayout.setVisibility(LinearLayout.GONE);
                    height.setText("");
                    height.requestFocus();
                    return;
                }
                if (ages > 110) {
                    Toast.makeText(PersonalInfo.this, "Please enter a valid Age value.", Toast.LENGTH_LONG).show();
                    linearLayout.setVisibility(LinearLayout.GONE);
                    age.setText("");
                    age.requestFocus();
                    return;
                }
                float hgtb = hgt / 100;
                float bmi = wgt / (hgtb * hgtb);
                double BMIFactor = ActivityFactor(text);
                int value = CriteriaFactor(weightcrt);
                double BMRDouble = ((10 * wgt) + (6.25 * hgt) - (5 * ages)) * BMIFactor;
                int bmr = (int)BMRDouble + value;
                String category = BMICalculate(bmi);
                linearLayout.setVisibility(LinearLayout.VISIBLE);
                txtResult.setText("Your BMI is " + bmi + ". You are " + category + " .Maximum intake of calories daily is " + bmr + " cal.");

                try{

                    JSONObject nameJSON = new JSONObject();
                    nameJSON.put("user_id", user.getId());
                    nameJSON.put("height",hgt);
                    nameJSON.put("weight",wgt);
                    nameJSON.put("bmi",bmi);
                    nameJSON.put("req_cal",bmr);

                    JSONArray colsList = new JSONArray();
                    colsList.put(nameJSON);

                    JSONObject args = new JSONObject();
                    args.put("table", "Personal_info");
                    args.put("objects", colsList);

                    JSONObject insertUserJSON = new JSONObject();
                    insertUserJSON.put("type", "insert");
                    insertUserJSON.put("args", args);

                    client.useDataService()
                            .setRequestBody(insertUserJSON)
                            .expectResponseType(InsertPersonalInfoResult.class)
                            .enqueue(new Callback<InsertPersonalInfoResult, HasuraException>() {
                                @Override
                                public void onSuccess(InsertPersonalInfoResult insertPersonalInfoResult) {
                                    Toast.makeText(getApplicationContext(), "Info added", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(HasuraException e) {
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                catch (JSONException e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.i("Auth-User-id",user.getId().toString());
        imgIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchUserId();
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        Toast.makeText(PersonalInfo.this, "Select a value", Toast.LENGTH_LONG).show();
    }
    public int CriteriaFactor(String weightcrt){
        int value=0;
        if (weightcrt.equals("Reduce 0.5kg per week")){
            value= -500;
        }
        if (weightcrt.equals("Maintain Weight")){
            value= 0;
        }
        if(weightcrt.equals("Increase 0.5kg per week")){
            value= 500;
        }
        return (value);
    }
    public String BMICalculate(float x){
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

    public double ActivityFactor(String item){
        if(item.equals("Sedentary")){
            return (1.2);
        }
        else if (item.equals("Lightly Active")){
            return(1.375);
        }
        else if(item.equals("Moderately Active")){
            return(1.55);
        }
        else if (item.equals("Very Active")){
            return(1.725);
        }
        else{
            return(1.9);
        }
    }
    private void fetchUserId(){
        try {
            JSONArray columnsArray = new JSONArray();
            columnsArray.put("user_id");

            JSONObject args = new JSONObject();
            args.put("table", "Personal_info");
            args.put("columns",columnsArray);

            JSONObject selectIntakeQuery = new JSONObject();
            selectIntakeQuery.put("type", "select");
            selectIntakeQuery.put("args", args);
            Log.i("IdRecord", selectIntakeQuery.toString());
            client.useDataService()
                    .setRequestBody(selectIntakeQuery)
                    .expectResponseTypeArrayOf(IdRecord.class)
                    .enqueue(new Callback<List<IdRecord>, HasuraException>() {
                        @Override
                        public void onSuccess(List<IdRecord> response) {
                            for (IdRecord record:response) {
                                Integer uid =record.getUser_id();
                                if(uid == user.getId()){
                                    Intent myIntent = new Intent(PersonalInfo.this, TotalIntakeView.class);
                                    startActivity(myIntent);
                                    return;
                                }
                            }
                            Toast.makeText(PersonalInfo.this,"Please fill the Personal Info form before proceeding to the next screen.",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(HasuraException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}