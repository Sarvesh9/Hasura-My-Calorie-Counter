package com.example.sarvesh.mycaloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import io.hasura.sdk.responseListener.AuthResponseListener;

public class Login extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPassword;
    private Button btnLogin;
    private Button btnNewUser;

    HasuraUser user;
    HasuraClient client;
    //defining AwesomeValidation object
    private AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        //init
        try {
            Hasura.setProjectConfig(new ProjectConfig.Builder()
                    .setCustomBaseDomain("newsman88.hasura-app.io")
                    //.enableOverHttp()
                    .build())
                    .initialise(this);
        } catch (HasuraInitException e) {
            e.printStackTrace();
        }
        client = Hasura.getClient();
        user = client.getUser();

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        btnLogin = (Button) findViewById(R.id.btnSubmitLogin);
        btnNewUser = (Button) findViewById(R.id.btnLoginRegister);

        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Login.this, SignUp.class);
                startActivity(myIntent);
            }
        });
        awesomeValidation.addValidation(this, R.id.editTextName, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        String regexPassword = ".{8,}";
        awesomeValidation.addValidation(this, R.id.editTextPassword,regexPassword, R.string.passworderror);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    private void login() {

        if (awesomeValidation.validate()) {

            user.setUsername(editTextName.getText().toString());
            user.setPassword(editTextPassword.getText().toString());

            user.login(new AuthResponseListener() {
                @Override
                public void onSuccess(String message) {
                    //Now Hasura.getClient().getCurrentUser() will have this user
                    Toast.makeText(Login.this, "Logged in", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(Login.this, TotalIntakeView.class);
                    startActivity(myIntent);
                }

                @Override
                public void onFailure(HasuraException e) {
                    //Handle Error
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
