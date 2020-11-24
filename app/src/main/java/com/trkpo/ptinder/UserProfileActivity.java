package com.trkpo.ptinder;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;

public class UserProfileActivity extends AppCompatActivity {
    TextView info, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        info = findViewById(R.id.info_user);
        id = findViewById(R.id.id_user);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            showInfo(signInAccount.getId());
        }
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
                Snackbar.make(findViewById(R.id.info_user), error.toString(), Snackbar.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
        id.setText(googleId);
    }
}
