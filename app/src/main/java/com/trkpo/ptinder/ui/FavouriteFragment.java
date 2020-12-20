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
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.pojo.PetInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;

public class FavouriteFragment extends Fragment {
    private View root;
    private Activity activity;
    private RecyclerView petCardRecycleView;
    private PetCardAdapter petCardAdapter;
    private String googleId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_favourite, container, false);
        activity = getActivity();
        googleId = GoogleSignIn.getLastSignedInAccount(activity).getId();

        initRecycleView();
        return root;
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
        String url = FAVOURITE_PATH + "/user/full/" + googleId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("VOLLEY", "Making get request (load pets): response - " + response.toString());
                            petCardAdapter.setItems(getPetsFromJSON(response));
                            petCardAdapter.setFavouriteFragment(true);
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
            String age = formAge(jsonObject.getInt("age"));
            String gender = jsonObject.getString("gender");
            String animalType = jsonObject.getJSONObject("animalType").getString("type");
            String purpose = jsonObject.getString("purpose");
            String comment = jsonObject.getString("comment");
            boolean isFavourite = true;
            PetInfo petInfo = new PetInfo(id, name, "", age, gender, animalType, purpose, comment, 3, isFavourite);

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
            petInfo.setOwnerInfo(ownerId, ownerName, ownerEmail);
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