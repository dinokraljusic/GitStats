package com.example.dinok.gitstats;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password, url;
    private Button login;
    private GithubApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       // }


        mApp = new GithubApp(this, Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.CALLBACK_URL);
        if (mApp.hasAccessToken())
            goToMain();
        else {
            username = (EditText) findViewById(R.id.username);
            password = (EditText) findViewById(R.id.password);
            url = (EditText) findViewById(R.id.url);
        }
    }

    public void Login(View view) {
        mApp.setListener(listener);
        mApp.setRepo(url.getText().toString());
        mApp.authorize();
    }

    public void goToMain() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
    }

    GithubApp.OAuthAuthenticationListener listener = new GithubApp.OAuthAuthenticationListener() {

        @Override
        public void onSuccess() {
            goToMain();
        }

        @Override
        public void onFail(String error) {
            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
        }
    };
}
