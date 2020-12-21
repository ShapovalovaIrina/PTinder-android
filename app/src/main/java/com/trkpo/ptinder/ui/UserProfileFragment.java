package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.config.PhotoTask;
import com.trkpo.ptinder.pojo.PetInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;
import static com.trkpo.ptinder.config.Constants.USER_ICON_URL;

public class UserProfileFragment extends Fragment {

    private Activity activity;
    private View root;

    private ImageView userIcon;
    private TextView username;
    private TextView location;
    private TextView phone;
    private TextView email;
    private RecyclerView petCardRecycleView;
    private PetCardAdapter petCardAdapter;
    private String googleId;
    private Button addPetBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_user_profile, container, false);
        activity = getActivity();
        userIcon = root.findViewById(R.id.user_icon);
        username = root.findViewById(R.id.username);
        location = root.findViewById(R.id.location);
        phone = root.findViewById(R.id.user_phone);
        email = root.findViewById(R.id.user_email);

        addPetBtn = root.findViewById(R.id.add_pet);
        addPetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.nav_pet_registration);
            }
        });

        try {
            userIcon.setImageBitmap(new PhotoTask().execute(USER_ICON_URL).get());
        } catch (ExecutionException | InterruptedException e) {
            Log.e("BITMAP", "Got error during bitmap parsing" + e.toString());
        }
        initUserInfo();
        initRecycleView();

        return root;
    }

    private void showInfo(String googleId) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = USERS_PATH + "/" + googleId;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        try {
                            username.setText(jsonResponse.getString("firstName") + " " + jsonResponse.getString("lastName"));
                            location.setText(jsonResponse.getString("address"));
                            email.setText(jsonResponse.getString("email"));
                            if (!jsonResponse.getString("number").equals("")) {
                                phone.setText(jsonResponse.getString("number"));
                            } else {
                                phone.setText("-");
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

    private void initUserInfo() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null) {
            showInfo(signInAccount.getId());
            googleId = signInAccount.getId();
        }
    }

    private void initRecycleView() {
        petCardRecycleView = root.findViewById(R.id.pet_cards_recycle_view);
        petCardRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        petCardAdapter = new PetCardAdapter();
        petCardRecycleView.setAdapter(petCardAdapter);

        loadFavouriteId();
    }

    private void loadFavouriteId() {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = FAVOURITE_PATH + "/user/id/" + googleId;

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d("VOLLEY", "Making get request (load favourite pets id): response - " + response.toString());
                            List<Long> favouritePetsId = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                favouritePetsId.add(Long.valueOf(response.get(i).toString()));
                            }
                            loadPets(favouritePetsId);
                        } catch (JSONException e) {
                            Log.e("VOLLEY", "Making get request (load favourite pets id): json error - " + e.toString());
                            Toast.makeText(activity, "JSON exception: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "Making get request (load favourite pets id): request error - " + error.toString());
                Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private void loadPets(final List<Long> favouritePetsId) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = PETS_PATH + "/owner/" + googleId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("VOLLEY", "Making get request (load pets): response - " + response.toString());
                            petCardAdapter.setItems(getPetsFromJSON(response, favouritePetsId));
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

    private Collection<PetInfo> getPetsFromJSON(String jsonString, List<Long> favouritePetsId) throws JSONException {
        Collection<PetInfo> pets = new ArrayList<>();
        JSONArray jArray = new JSONArray(jsonString);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonObject = jArray.getJSONObject(i);
            Long id = jsonObject.getLong("petId");
            String name = jsonObject.getString("name");
            String breed = jsonObject.getString("breed");
            String age = formAge(jsonObject.getInt("age"));
            String gender = jsonObject.getString("gender");
            String animalType = jsonObject.getJSONObject("animalType").getString("type");
            String purpose = jsonObject.getString("purpose");
            String comment = jsonObject.getString("comment");
            boolean isFavourite = favouritePetsId.contains(id);
            PetInfo petInfo = new PetInfo(id, name, breed, age, gender, animalType, purpose, comment, 1, isFavourite);

            JSONArray images = jsonObject.getJSONArray("petPhotos");
            if (images != null) {
                for (int j = 0; j < images.length(); j++) {
                    String imageStr = images.getJSONObject(j).getString("photo");
                    byte[] imageBytes = Base64.decode(imageStr, Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    petInfo.addIcon(image);
                }
            }
            pets.add(petInfo);

            JSONObject ownerInfo = jsonObject.getJSONObject("owner");
            String ownerId = ownerInfo.getString("googleId");
            String ownerName = ownerInfo.getString("firstName") + " " + ownerInfo.getString("lastName");
            String ownerEmail = ownerInfo.getString("email");
            String ownerIconURL = ownerInfo.getString("photoUrl");
            petInfo.setOwnerInfo(ownerId, ownerName, ownerEmail, ownerIconURL);

            petInfo.setCurrentUserInfo(googleId);
        }
        return pets;
    }

    private String formAge(Integer age) {
        String year = "";
        switch (age) {
            case 1:
                year = " год";
                break;
            case 2:
            case 3:
            case 4:
                year = " года";
                break;
            default:
                year = " лет";
        }
        return age + year;
    }
}
