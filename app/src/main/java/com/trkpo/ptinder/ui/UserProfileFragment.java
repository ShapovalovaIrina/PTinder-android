package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.pojo.PetInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.SERVER_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class UserProfileFragment extends Fragment {

    private Activity activity;
    private View root;
    private TextView info;
    private TextView username;
    private TextView location;
    private TextView phone;
    private TextView email;
    private RecyclerView petCardRecycleView;
    private PetCardAdapter petCardAdapter;
    private String googleId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_user_profile, container, false);
        activity = getActivity();
        username = root.findViewById(R.id.username);
        location = root.findViewById(R.id.location);
        phone = root.findViewById(R.id.user_phone);
        email = root.findViewById(R.id.user_email);
        info = root.findViewById(R.id.user_info);

        initUserInfo();
        initRecycleView();

        return root;
    }

    private void showInfo(String googleId) {
        if (activity != null) {
            RequestQueue queue = Volley.newRequestQueue(activity);
            String url = USERS_PATH + "/google/" + googleId;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            info.setText(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        }
    }

    private void initUserInfo() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null) {
//            if (signInAccount.getPhotoUrl() != null) { user_icon.setImageURI(signInAccount.getPhotoUrl()); }
            username.setText(signInAccount.getDisplayName());
            location.setText("Ваш город");
            phone.setText("Ваш телефон");
            email.setText(signInAccount.getEmail());
            showInfo(signInAccount.getId());
            googleId = signInAccount.getId();
        }
    }

    private void initRecycleView() {
        petCardRecycleView = root.findViewById(R.id.pet_cards_recycle_view);
        petCardRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        petCardAdapter = new PetCardAdapter();
        petCardRecycleView.setAdapter(petCardAdapter);

        loadPets();
    }

    private void loadPets() {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = PETS_PATH + "/owner/" + googleId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            petCardAdapter.setItems(getPetsFromJSON(response));
                        } catch (JSONException e) {
                            Snackbar.make(root.findViewById(R.id.user_icon), e.toString(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(root.findViewById(R.id.user_info), error.toString(), Snackbar.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private Collection<PetInfo> getPetsFromJSON(String jsonString) throws JSONException {
        Collection<PetInfo> pets = new ArrayList<>();
        JSONArray jArray = new JSONArray(jsonString);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonObject = jArray.getJSONObject(i);
            String name = jsonObject.getString("name");
            String age = String.valueOf(jsonObject.getInt("age"));
            String breed = jsonObject.getString("breed");
            String gender = jsonObject.getString("gender");
            String purpose = jsonObject.getString("purpose");
            String comment = jsonObject.getString("comment");
            PetInfo petInfo = new PetInfo(name, breed, age, gender, purpose, comment);

            JSONArray images = jsonObject.getJSONArray("petPhotos");
            if (images != null && images.length() > 0) {
                //  Set first photo as icon
                String imageStr = images.getJSONObject(0).getString("photo");
                byte[] imageBytes = Base64.decode(imageStr, Base64.DEFAULT);
                Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                petInfo.setIcon(image);
            }
            pets.add(petInfo);
        }
        return pets;
    }
}
