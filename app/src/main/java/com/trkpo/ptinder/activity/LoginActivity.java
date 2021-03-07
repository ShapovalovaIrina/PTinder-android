package com.trkpo.ptinder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.HTTP.Connection;

import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Activity activity;

    private SignInButton signInButton;

    /* Variable for testing*/
    private String optUrl;
    private boolean connectionPermission;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        this.activity = this;

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        createRequest();

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAction();
            }
        });
    }

    public void onClickAction() {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        if (currentUser != null) {
            isUserExists(GoogleSignIn.getLastSignedInAccount(getApplicationContext()));
        } else {
            signIn();
        }
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            isUserExists(GoogleSignIn.getLastSignedInAccount(getApplicationContext()));
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(findViewById(R.id.login_activity), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    public void isUserExists(GoogleSignInAccount signInAccount) {
        if (signInAccount == null) return;

        if (!Connection.hasConnection(this) || !connectionPermission) {
            Toast.makeText(this, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String googleId = signInAccount.getId();
        String url = optUrl == null ? USERS_PATH + "/exists/" + googleId : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            String response = new GetRequest().execute(url).get();
            if (response.equals("true")) {
                Log.d("VOLLEY", "Making get request (is user exists): user exists in DB");
                startNavigationActivity();
            } else if (response.equals("false")){
                Log.d("VOLLEY", "Making get request (is user exists): user dost NOT exists in DB");
                startLoginUserActivity();
            } else {
                Log.e("VOLLEY", "Making get request (is user exists): INCORRECT RESPONSE - " + response);
            }
        } catch (ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Making get request (is user exists): request error - " + error.toString());
            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void startLoginUserActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginUserInfoActivity.class);
        startActivity(intent);
        finish();
    }

    private void startNavigationActivity() {
        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
        startActivity(intent);
        finish();
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public void setOptUrlAndConnectionPermission(String optUrl, boolean connectionPermission) {
        this.optUrl = optUrl;
        this.connectionPermission = connectionPermission;
    }

    public void resetOptUrlAndConnectionPermission() {
        optUrl = null;
        connectionPermission = true;
    }

    public String getOptUrl() {
        return optUrl;
    }
}
