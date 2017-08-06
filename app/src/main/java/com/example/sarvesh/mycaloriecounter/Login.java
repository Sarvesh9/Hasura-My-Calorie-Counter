package com.example.sarvesh.mycaloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.hasura.sdk.Hasura;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init
        try {
            Hasura.setProjectConfig(new ProjectConfig.Builder()
                    .setCustomBaseDomain("newsman88.hasura-app.io").enableOverHttp()
                    .build())
                    .initialise(this);
        } catch (HasuraInitException e) {
            e.printStackTrace();
        }
        user = Hasura.getClient().getUser();

        editTextName = (EditText) findViewById(R.id.editName);
        editTextPassword = (EditText) findViewById(R.id.editPassword);
        btnLogin = (Button) findViewById(R.id.btnSubmitLogin);
        btnNewUser = (Button) findViewById(R.id.btnLoginRegister);

        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Login.this, SignUp.class);
                startActivity(myIntent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    private void login() {
        String username = editTextName.getText().toString();
        String password = editTextPassword.getText().toString();

        user.setUsername(username);
        user.setPassword(password);
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
