package com.trkpo.ptinder.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.trkpo.ptinder.config.Constants.PETS_PATH;

public class PetRegistrationActivity extends AppCompatActivity {

    private TextView petName;
    private TextView petAge;
    private Spinner petGender;
    private Spinner petType;
    private String googleId;

    private Bitmap imageBitmap;

    static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_registration);
        petName = findViewById(R.id.pet_name);
        petAge = findViewById(R.id.pet_age);
        petGender = findViewById(R.id.gender_spinner);
        petType = findViewById(R.id.type_spinner);

        initUserInfo();
    }

    private void initUserInfo() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (signInAccount != null) {
            googleId = signInAccount.getId();
        }
    }

    private byte[] getByteArrayfromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos);
        return bos.toByteArray();
    }

    public void onSavePetClick(View view) {
        JSONObject requestObject = new JSONObject();
        JSONObject jsonBodyWithPet = new JSONObject();
        try {
            jsonBodyWithPet.put("name", petName.getText());
            jsonBodyWithPet.put("age", petAge.getText());
            jsonBodyWithPet.put("gender", translateGender((String) petGender.getSelectedItem()));
            jsonBodyWithPet.put("type", translateType((String) petType.getSelectedItem()));
            if (imageBitmap != null) {
                byte[] imageBytes = getByteArrayfromBitmap(imageBitmap);
                String imageStr = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                jsonBodyWithPet.put("photo", imageStr);
            }
            requestObject.put("googleId", googleId);
            requestObject.put("pet", jsonBodyWithPet);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = requestObject.toString();
        Log.i("REGISTRATION", "Going to register pet with request: " + requestBody);

        if (getApplicationContext() != null) {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, PETS_PATH, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            queue.add(stringRequest);
        }

    }

    private String translateType(String type) {
        if (type.equals("Кот")) {
            return "CAT";
        }
        if (type.equals("Собака")) {
            return "DOG";
        }
        if (type.equals("Рыбка")) {
            return "FISH";
        }
        if (type.equals("Хомяк")) {
            return "HAMSTER";
        }
        if (type.equals("Мышь")) {
            return "MOUSE";
        }
        if (type.equals("Крыса")) {
            return "RAT";
        }
        if (type.equals("Змея")) {
            return "SNAKE";
        }
        if (type.equals("Попугай")) {
            return "PARROT";
        }
        return "";
    }

    private String translateGender(String gender) {
        return gender.equals("Мужской") ? "MALE" : "FEMALE";
    }

    public void onDownloadImgClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        ImageView imageView = (ImageView) findViewById(R.id.pet_icon);

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(imageBitmap);
                }
        }
    }
}
