package com.trkpo.ptinder.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.trkpo.ptinder.R;

public class UserProfileActivity extends AppCompatActivity {
    TextView info;
    ImageView user_icon;
    TextView username;
    TextView location;
    TextView phone;
    TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initUserInfo();

        Button showPets = findViewById(R.id.show_pets);
        showPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, PetsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showInfo(String googleId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.0.102:8080/user/" + googleId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        info.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(findViewById(R.id.user_info), error.toString(), Snackbar.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private void initUserInfo() {
        user_icon = findViewById(R.id.user_icon);
        username = findViewById(R.id.username);
        location = findViewById(R.id.location);
        phone = findViewById(R.id.user_phone);
        email = findViewById(R.id.user_email);
        info = findViewById(R.id.user_info);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            if (signInAccount.getPhotoUrl() != null) { user_icon.setImageURI(signInAccount.getPhotoUrl()); }
            username.setText(signInAccount.getDisplayName());
            location.setText("Ваш город");
            phone.setText("Ваш телефон");
            email.setText(signInAccount.getEmail());
            showInfo(signInAccount.getId());
        }
    }
}