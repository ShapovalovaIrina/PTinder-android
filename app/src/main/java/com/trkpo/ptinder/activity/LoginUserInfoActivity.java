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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.Gender;
import com.trkpo.ptinder.HTTP.Connection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class LoginUserInfoActivity extends AppCompatActivity {
    private EditText cityEdit;
    private EditText phoneEdit;
    private Gender gender;
    private String city;
    private String phone;

    private Button submitButton;
    private RadioGroup rg;
    private GoogleSignInAccount signInAccount;

    /* Variable for testing*/
    private String optUrl;
    private boolean connectionPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_info);

        cityEdit = findViewById(R.id.editPersonCity);
        phoneEdit = findViewById(R.id.editPersonPhone);
        submitButton = findViewById(R.id.login_submit_button);
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
        rg = findViewById(R.id.radio_group_gender);
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

    public void postUserInfo() {
        if (!Connection.hasConnection(this) || !connectionPermission) {
            Toast.makeText(this, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl == null ? USERS_PATH : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            if (signInAccount == null) setGoogleSignInAccount();
            JSONObject postData = new JSONObject();
            String[] userFullName = signInAccount.getDisplayName().split(" ");
            postData.put("firstName", userFullName[0]);
            postData.put("lastName", userFullName[1]);
            postData.put("gender", gender);
            postData.put("number", phone);
            postData.put("address", city);
            postData.put("email", signInAccount.getEmail());
            postData.put("photoUrl", signInAccount.getPhotoUrl());
            postData.put("contactInfoPublic", false);
            postData.put("googleId", signInAccount.getId());

            String response = new PostRequest().execute(new PostRequestParams(url, postData.toString())).get();

            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
            startActivity(intent);
            finish();
            Log.d("VOLLEY", "Success response: " + response);
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.d("VOLLEY", "Not Success response: " + error.toString());
        }
    }

    public Button getSubmitButton() {
        return submitButton;
    }

    public RadioGroup getRg() {
        return rg;
    }

    public EditText getCityEdit() {
        return cityEdit;
    }

    public void setGoogleSignInAccount(GoogleSignInAccount ... googleSignInAccounts) {
        if (googleSignInAccounts.length != 0)
            signInAccount =  googleSignInAccounts[0];
        else
            signInAccount = GoogleSignIn.getLastSignedInAccount(this);
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
