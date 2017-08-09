package com.example.sarvesh.mycaloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

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
import io.hasura.sdk.responseListener.SignUpResponseListener;

public class SignUp extends AppCompatActivity implements OnItemSelectedListener {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button submit;
    HasuraUser user;
    HasuraClient client;
    private Spinner spinner1;
    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        spinner1 = (Spinner) findViewById(R.id.gender);

        spinner1.setOnItemSelectedListener(this);

        List<String> genders = new ArrayList<String>();
        genders.add("Male");
        genders.add("Female");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, genders);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(dataAdapter);

        try {
            Hasura.setProjectConfig(new ProjectConfig.Builder()
                    .setCustomBaseDomain("newsman88.hasura-app.io")
//                    .enableOverHttp()
                    .build())
                    .initialise(this);
        } catch (HasuraInitException e) {
            e.printStackTrace();
        }
        client = Hasura.getClient();
        user = client.getUser();

        editTextName = (EditText) findViewById(R.id.editName);
        editTextEmail = (EditText) findViewById(R.id.editEmail);
        editTextPassword = (EditText) findViewById(R.id.editPassword);
        submit = (Button) findViewById(R.id.btnSubmit);

        awesomeValidation.addValidation(this, R.id.editName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.editEmail, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        String regexPassword = ".{8,}";
        awesomeValidation.addValidation(this, R.id.editPassword,regexPassword, R.string.passworderror);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        if (!item.equals("Select Gender")){
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
        Toast.makeText(SignUp.this, "Select a Gender", Toast.LENGTH_LONG).show();
    }
        /**
         * Validating form
         */

        private void submitForm() {
            //first validate the form then move ahead
            //if this becomes true that means validation is successful
            if (awesomeValidation.validate()) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show();
                //process the data further
                final String username = editTextName.getText().toString();
                String password = editTextPassword.getText().toString();
                String email = editTextEmail.getText().toString();
                final String gender = spinner1.getSelectedItem().toString();

                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setMobile("8861503583");
                user.signUp(new SignUpResponseListener() {
                    @Override
                    public void onSuccessAwaitingVerification(HasuraUser user) {
                        //The user is registered on Hasura, but either his mobile or email needs to be verified.
                        Toast.makeText(getApplicationContext(), "Registered. Please verify your email.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(HasuraUser user) {

                        try{

                        JSONObject nameJSON = new JSONObject();
                        nameJSON.put("name", username);
                        nameJSON.put("gender",gender);

                        JSONArray colsList = new JSONArray();
                        colsList.put(nameJSON);

                        JSONObject args = new JSONObject();
                        args.put("table", "User");
                        args.put("objects", colsList);

                        JSONObject insertUserJSON = new JSONObject();
                        insertUserJSON.put("type", "insert");
                        insertUserJSON.put("args", args);

                        client.useDataService()
                                .setRequestBody(insertUserJSON)
                                .expectResponseType(RegisterUserResponse.class)
                                .enqueue(new Callback<RegisterUserResponse, HasuraException>() {
                                    @Override
                                    public void onSuccess(RegisterUserResponse registerUserResponse) {
                                        Toast.makeText(getApplicationContext(), "User Created ", Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(getApplicationContext(), "Thank You! ", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(SignUp.this, PersonalInfo.class);
                        startActivity(myIntent);
                    }

                    @Override
                    public void onFailure(HasuraException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
}
