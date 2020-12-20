package com.trkpo.ptinder.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.Gender;

import org.json.JSONException;
import org.json.JSONObject;

import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.SERVER_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class LoginUserInfoActivity extends AppCompatActivity {
    private EditText cityEdit;
    private EditText phoneEdit;
    private Gender gender;
    private String city;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_info);

        cityEdit = findViewById(R.id.editPersonCity);
        phoneEdit = findViewById(R.id.editPersonPhone);
        Button submitButton = findViewById(R.id.login_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = cityEdit.getText().toString();
                phone = phoneEdit.getText().toString();
                if (city.equals("") || gender == null) {
                    Toast.makeText(getApplicationContext(), "Необходимо указать все поля, которые отмечены *", Toast.LENGTH_LONG).show();
                    return;
                }
                postUserInfo();
            }
        });
        RadioGroup rg = findViewById(R.id.radio_group_gender);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_button_female:
                        gender = Gender.FEMALE;
                        break;
                    case R.id.radio_button_male:
                        gender = Gender.MALE;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void postUserInfo() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = USERS_PATH;

        JSONObject postData = new JSONObject();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        String[] userFullName = signInAccount.getDisplayName().split(" ");
        try {
            postData.put("firstName", userFullName[0]);
            postData.put("lastName", userFullName[1]);
            postData.put("gender", gender);
            postData.put("number", phone);
            postData.put("address", city);
            postData.put("email", signInAccount.getEmail());
            postData.put("photoUrl", signInAccount.getPhotoUrl());
            postData.put("contactInfoPublic", true);
            postData.put("googleId", signInAccount.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d("VOLLEY", "Success response: " + response);
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", "Not Success response: " + error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }
}
