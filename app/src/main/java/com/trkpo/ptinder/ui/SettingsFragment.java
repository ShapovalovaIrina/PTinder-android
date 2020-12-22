package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.activity.LoginActivity;
import com.trkpo.ptinder.adapter.SmallPetAdapter;
import com.trkpo.ptinder.pojo.PetInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class SettingsFragment extends Fragment {
    private Activity activity;
    private View root;

    private TextView firstName;
    private TextView lastName;
    private TextView location;
    private TextView phone;
    private TextView email;
    private RadioGroup gender;
    private Button updateUser;
    private Button deleteUser;
    private Button logoutUser;

    private RecyclerView smallPetRecycleView;
    private SmallPetAdapter smallPetAdapter;

    private String googleId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);
        activity = getActivity();

        firstName = root.findViewById(R.id.user_first_name);
        lastName = root.findViewById(R.id.user_last_name);
        phone = root.findViewById(R.id.user_phone);
        email = root.findViewById(R.id.user_email);
        location = root.findViewById(R.id.user_address);
        gender = root.findViewById(R.id.radio_group_gender_user);
        updateUser = root.findViewById(R.id.update_user);
        deleteUser = root.findViewById(R.id.delete_user);
        logoutUser = root.findViewById(R.id.logout);

        initUserInfo();
        initRecycleView();

        updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null) {
                    RequestQueue queue = Volley.newRequestQueue(activity);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("firstName", firstName.getText());
                        jsonObject.put("lastName", lastName.getText());
                        jsonObject.put("gender", gender.getCheckedRadioButtonId() == 0 ? "FEMALE" : "MALE");
                        jsonObject.put("email", email.getText());
                        jsonObject.put("number", phone.getText());
                        jsonObject.put("address", location.getText());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String requestBody = jsonObject.toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.PUT, USERS_PATH + "/" + googleId, new Response.Listener<String>() {
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
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(activity);
                final String requestBody = "";
                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, USERS_PATH + "/" + googleId, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("volley", response);
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
                            responseString = String.valueOf(response.statusCode); // can get more details such as response.headers
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };
                queue.add(stringRequest);
                startActivity(new Intent(activity, LoginActivity.class));
            }
        });

        logoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        return root;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(activity.getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        activity.finish();
                    }
                });
    }

    private void initUserInfo() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null) {
            showInfo(signInAccount.getId());
            googleId = signInAccount.getId();
        }
    }

    private void initRecycleView() {
        smallPetRecycleView = root.findViewById(R.id.small_pet_cards_recycle_view);
        smallPetRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        smallPetAdapter = new SmallPetAdapter();
        smallPetRecycleView.setAdapter(smallPetAdapter);

        loadPets();
    }

    private void showInfo(String googleId) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = USERS_PATH + "/" + googleId;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        try {
                            firstName.setText(jsonResponse.getString("firstName"));
                            lastName.setText(jsonResponse.getString("lastName"));
                            location.setText(jsonResponse.getString("address"));
                            email.setText(jsonResponse.getString("email"));
                            if (!jsonResponse.getString("number").equals("")) {
                                phone.setText(jsonResponse.getString("number"));
                            } else {
                                phone.setText("-");
                            }
                            if (jsonResponse.getString("gender").equalsIgnoreCase("female")) {
                                gender.check(R.id.radio_button_female_user);
                            } else {
                                gender.check(R.id.radio_button_male_user);

                            }
                        } catch (JSONException e) {
                            Log.e("VOLLEY", "Making get request (get user by google id): json error - " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "Making get request (get user by google id): request error - " + error.toString());
            }
        });
        queue.add(stringRequest);
    }

    private void loadPets() {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = PETS_PATH + "/owner/" + googleId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            smallPetAdapter.setItems(getPetsFromJSON(response));
                        } catch (JSONException e) {
                            Log.e("VOLLEY", "Making get request (load pets): json error - " + e.toString());
                            Toast.makeText(activity, "JSON exception: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "Making get request (load pets): request error - " + error.toString());
                Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private Collection<PetInfo> getPetsFromJSON(String jsonString) throws JSONException {
        Collection<PetInfo> pets = new ArrayList<>();
        JSONArray jArray = new JSONArray(jsonString);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonObject = jArray.getJSONObject(i);
            Long id = jsonObject.getLong("petId");
            String name = jsonObject.getString("name");
            String breed = jsonObject.getString("breed");
            String age = "" + jsonObject.getInt("age");
            String gender = jsonObject.getString("gender");
            String animalType = jsonObject.getJSONObject("animalType").getString("type");
            String purpose = jsonObject.getString("purpose");
            String comment = jsonObject.getString("comment");

            List<Bitmap> icons = new ArrayList<>();
            JSONArray images = jsonObject.getJSONArray("petPhotos");
            if (images != null) {
                for (int j = 0; j < images.length(); j++) {
                    String imageStr = images.getJSONObject(j).getString("photo");
                    byte[] imageBytes = Base64.decode(imageStr, Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    icons.add(image);
                }
            }

            PetInfo petInfo = new PetInfo(id, name, breed, age, gender, animalType, purpose, comment, icons, -1, false);
            pets.add(petInfo);
        }
        return pets;
    }

}
