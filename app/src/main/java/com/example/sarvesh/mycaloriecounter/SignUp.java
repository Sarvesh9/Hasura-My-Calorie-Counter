package com.example.sarvesh.mycaloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import io.hasura.sdk.Hasura;
import io.hasura.sdk.HasuraClient;
import io.hasura.sdk.HasuraUser;
import io.hasura.sdk.ProjectConfig;
import io.hasura.sdk.exception.HasuraException;
import io.hasura.sdk.exception.HasuraInitException;
import io.hasura.sdk.responseListener.SignUpResponseListener;

public class SignUp extends AppCompatActivity{

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextMobile;

    private Button submit;
    HasuraUser user;
    HasuraClient client;
    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        try {
            Hasura.setProjectConfig(new ProjectConfig.Builder()
                    .setCustomBaseDomain("hasuramycaloriecounter.hasura.me").enableOverHttp()
                    .build())
                    .initialise(this);
        } catch (HasuraInitException e) {
            e.printStackTrace();
        }
        HasuraClient client = Hasura.getClient();
        user = client.getUser();

        editTextName = (EditText) findViewById(R.id.editName);
        editTextEmail = (EditText) findViewById(R.id.editEmail);
        editTextPassword = (EditText) findViewById(R.id.editPassword);
        editTextMobile = (EditText) findViewById(R.id.editMobileNo);
        submit = (Button) findViewById(R.id.btnSubmit);

        awesomeValidation.addValidation(this, R.id.editName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.editEmail, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        String regexPassword = ".{8,}";
        awesomeValidation.addValidation(this, R.id.editPassword,regexPassword, R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.editMobileNo, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

        /**
         * Validating form
         */

        private void submitForm() {
            //first validate the form then move ahead
            //if this becomes true that means validation is successfull
            if (awesomeValidation.validate()) {
                Toast.makeText(this, "Registration Successfull", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(SignUp.this, Login.class);
                startActivity(myIntent);
                //process the data further
                String username = editTextName.getText().toString();
                String password = editTextPassword.getText().toString();
                String email = editTextEmail.getText().toString();
                String mobile = editTextMobile.getText().toString();

                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setMobile(mobile);
                user.signUp(new SignUpResponseListener() {
                    @Override
                    public void onSuccessAwaitingVerification(HasuraUser user) {
                        //The user is registered on Hasura, but either his mobile or email needs to be verified.
                        Toast.makeText(getApplicationContext(), "Registered. Please verify your email.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(HasuraUser user) {
                        //Now Hasura.getClient().getCurrentUser() will have this user
//                        try {
                            //JSON query for inserting user (REFERENCE)
//                    {
//                        "type" : "insert",
//                        "args" : {
//                            "table" : "user_info",
//                            "objects": [{"name" : "username"}]
//                        }
//                    }

//                        JSONObject nameJSON = new JSONObject();
//                        nameJSON.put("name", username);
//
//                        JSONArray colsList = new JSONArray();
//                        colsList.put(nameJSON);
//
//                        JSONObject args = new JSONObject();
//                        args.put("table", "user_info");
//                        args.put("objects", colsList);

//                        JSONObject insertUserJSON = new JSONObject();
//                        insertUserJSON.put("type", "insert");
//                        insertUserJSON.put("args", args);

//                            client.useDataService()
//                               .setRequestBody(insertUserJSON)
//                                    .expectResponseType(RegisterUserResponse.class)
//                                    .enqueue(new Callback<RegisterUserResponse, HasuraException>() {
//                                        @Override
//                                        public void onSuccess(RegisterUserResponse registerUserResponse) {
//                                            Toast.makeText(getApplicationContext(), "User Created ", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                        @Override
//                                        public void onFailure(HasuraException e) {
//                                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                        } catch (JSONException e) {
//                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
//                        }
                        Toast.makeText(getApplicationContext(), "Thank You! ", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(SignUp.this, Login.class);
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
