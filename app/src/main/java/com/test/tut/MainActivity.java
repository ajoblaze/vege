package com.test.tut;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private String TAG = "exc";
    private TextView tv;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        tv = (TextView) findViewById(R.id.text);

        // check if user is logged in
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null){
            tv.setText("User ID: "
                    + accessToken.getUserId()
                    + "\n" +
                    "Auth Token: "
                    + accessToken.getToken());
        }else{
        }

        // listen to login
        LoginButton btn = (LoginButton) findViewById(R.id.login_button);
        btn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String s = "User ID: "
                        + loginResult.getAccessToken().getUserId()
                        + "\n" +
                        "Auth Token: "
                        + loginResult.getAccessToken().getToken();
                tv.setText(s);
                accessTokenTracker.startTracking(); // start fb logout listener
            }

            @Override
            public void onCancel() {
                tv.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException error) {
                tv.setText("Login attempt error.");
            }
        });

        // listen to logout
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    tv.setText("Successfully Logged Out");
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
