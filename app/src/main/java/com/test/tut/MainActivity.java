package com.test.tut;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private CallbackManager callbackManager;
    private String TAG = "exc";
    private TextView tv;
    private AccessTokenTracker accessTokenTracker;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 007;
    private SignInButton signInButton;
    private Button signOutButton;
    private Button revokeAccessButton;
    private ImageView imgProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.text);

        ////////////////////////////////// FACEBOOK LOGIN ////////////////////////////////////////////////

        callbackManager = CallbackManager.Factory.create();

        // check if user is logged in
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            tv.setText("User ID: "
                    + accessToken.getUserId()
                    + "\n" +
                    "Auth Token: "
                    + accessToken.getToken());
        } else {
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

        ////////////////////////////////// GOOGLE PLUS LOGIN ////////////////////////////////////////////////

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signOutButton = (Button) findViewById(R.id.sign_out_button);
        revokeAccessButton = (Button) findViewById(R.id.revoke_access_button);
        imgProfilePic = (ImageView) findViewById(R.id.img_profile_pic);

        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        revokeAccessButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data); // FB

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.sign_out_button:
                signOut();
                break;

            case R.id.revoke_access_button:
                revokeAccess();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String s = "display name: " + acct.getDisplayName();

            String personName = acct.getDisplayName();
//            String personPhotoUrl = acct.getPhotoUrl().toString();
            String email = acct.getEmail();

            s += ", name: " + personName + ", email: " + email;

            tv.setText(s);
//            Glide.with(getApplicationContext()).load(personPhotoUrl)
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imgProfilePic);

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            revokeAccessButton.setVisibility(View.VISIBLE);
//            llProfileLayout.setVisibility(View.VISIBLE);
        } else {
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            revokeAccessButton.setVisibility(View.GONE);
//            llProfileLayout.setVisibility(View.GONE);
        }
    }
}
