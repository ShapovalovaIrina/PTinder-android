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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.pojo.PetInfo;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.SEARCH_PATH;

public class SearchFragment extends Fragment {
    private Activity activity;
    private View root;

    private Spinner addressSpinner;
    private Spinner typeSpinner;
    private Spinner purposeSpinner;
    private RadioButton maleRB;
    private RadioButton femaleRB;
    private RadioGroup radioGroup;
    private TextView minAge;
    private TextView maxAge;
    private Button searchBtn;

    private RecyclerView petCardRecycleView;
    private PetCardAdapter petCardAdapter;

    private List<Long> favouritePetsId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        activity = getActivity();
        addressSpinner = root.findViewById(R.id.location_spinner);
        typeSpinner = root.findViewById(R.id.type_spinner);
        purposeSpinner = root.findViewById(R.id.purpose_spinner);
        maleRB = root.findViewById(R.id.radio_button_male);
        femaleRB = root.findViewById(R.id.radio_button_female);
        minAge = root.findViewById(R.id.min_pet_age);
        maxAge = root.findViewById(R.id.max_pet_age);
        searchBtn = root.findViewById(R.id.search);
        radioGroup = root.findViewById(R.id.sex_buttons_group);

        petCardRecycleView = root.findViewById(R.id.pet_cards_recycle_view);
        petCardRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        petCardAdapter = new PetCardAdapter();
        petCardRecycleView.setAdapter(petCardAdapter);

        favouritePetsId = new ArrayList<>();
        loadFavouriteId();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        RequestQueue queue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, SEARCH_PATH + "/address",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("INFO", "GOT " + response);
                        adapter.addAll(getAddrFromJSON(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "Making get request (load address): request error - " + error.toString());
                Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
        addressSpinner.setAdapter(adapter);

        final ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stringRequest = new StringRequest(Request.Method.GET, PETS_PATH + "/types",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("INFO", "GOT " + response);
                        try {
                            typeAdapter.addAll(getTypesFromJSON(response));
                        } catch (JSONException e) {
                            Log.e("JSON", "Got error during json parsing" + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "Making get request (load types): request error - " + error.toString());
                Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
        typeSpinner.setAdapter(typeAdapter);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = "" + addressSpinner.getSelectedItem();
                String gender = translateGender(radioGroup);
                String purpose = translatePurpose("" + purposeSpinner.getSelectedItem());
                String type = "" + (typeSpinner.getSelectedItem() != null ? typeSpinner.getSelectedItem() : "");
                petCardAdapter.clearItems();

                if (activity != null) {
                    RequestQueue queue = Volley.newRequestQueue(activity);
                    String url = "";
                    try {
                        url = SEARCH_PATH
                                + "?" + "address=" + encodeValue(address)
                                + "&" + "gender=" + gender
                                + "&" + "purpose=" + purpose
                                + "&" + "type=" + type
                                + "&" + "minAge=" + minAge.getText()
                                + "&" + "maxAge=" + maxAge.getText();
                    } catch (UnsupportedEncodingException e) {
                        Log.e("ENCODING", "Got error during url encoding " + e.toString());
                    }

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                petCardAdapter.setItems(getPetsFromJSON(response));
                            } catch (JSONException e) {
                                Log.e("JSON", "Got error during json parsing" + e.toString());

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", error.toString());
                        }
                    });
                    queue.add(stringRequest);
                }
            }
        });

        return root;
    }

    private Collection<String> getTypesFromJSON(String response) throws JSONException {
        List<String> types = new ArrayList<>();
        types.add("-");
        JSONArray jArray = new JSONArray(response);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonObject = jArray.getJSONObject(i);
            types.add((String) jsonObject.get("type"));
        }
        return types;
    }

    private String translateGender(RadioGroup gender) {
        RadioButton myRadioButton = root.findViewById(gender.getCheckedRadioButtonId());
        if (myRadioButton != null) {
            return myRadioButton.getText().equals("Мужской") ? "MALE" : "FEMALE";
        } else {
            return "";
        }
    }

    private String translatePurpose(String purpose) {
        if (purpose.equals("Прогулка")) {
            return "WALKING";
        }
        if (purpose.equals("Вязка")) {
            return "BREEDING";
        }
        if (purpose.equals("Переливание крови")) {
            return "DONORSHIP";
        }
        if (purpose.equals("Дружба")) {
            return "FRIENDSHIP";
        }
        if (purpose.equals("-")) {
            return "NOTHING";
        }
        return "";
    }

    private Collection<PetInfo> getPetsFromJSON(String jsonString) throws JSONException {
        String googleId = GoogleSignIn.getLastSignedInAccount(activity).getId();
        Collection<PetInfo> pets = new ArrayList<>();
        JSONArray jArray = new JSONArray(jsonString);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonObject = jArray.getJSONObject(i);
            Long id = jsonObject.getLong("petId");
            String name = jsonObject.getString("name");
            String age = jsonObject.getString("age");
            String breed = jsonObject.getString("breed");
            String gender = jsonObject.getString("gender");
            String animalType = jsonObject.getJSONObject("animalType").getString("type");
            String purpose = jsonObject.getString("purpose");
            String comment = jsonObject.getString("comment");
            boolean isFavourite = favouritePetsId.contains(id);

            List<Bitmap> icons = new ArrayList<>();
            JSONArray images = jsonObject.getJSONArray("petPhotos");
            if (images != null && images.length() > 0) {
                //  Set first photo as icon
                String imageStr = images.getJSONObject(0).getString("photo");
                byte[] imageBytes = Base64.decode(imageStr, Base64.DEFAULT);
                Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                icons.add(image);
            }
            PetInfo petInfo = new PetInfo(id, name, breed, age, gender, animalType, purpose, comment, icons, 2, isFavourite);
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

    private String form(String animalType) {
        return animalType != null ? StringUtils.substringBetween(animalType, "\"type\":\"", "\"") : "";
    }

    private Collection<String> getAddrFromJSON(String response) {
        ArrayList<String> list = new ArrayList<>();
        list.add("-");
        response = response.replaceAll("\"", "");
        response = response.replaceAll("\\[", "");
        response = response.replaceAll("]", "");
        list.addAll(Arrays.asList(response.split(",")));
        return list;
    }

    private String encodeValue(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    private void loadFavouriteId() {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String googleId = GoogleSignIn.getLastSignedInAccount(activity).getId();
        String url = FAVOURITE_PATH + "/user/id/" + googleId;

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d("VOLLEY", "Making get request (load favourite pets id): response - " + response.toString());
                            for (int i = 0; i < response.length(); i++) {
                                favouritePetsId.add(Long.valueOf(response.get(i).toString()));
                            }
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
}