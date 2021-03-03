package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.os.Bundle;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.utils.Connection;
import com.trkpo.ptinder.utils.GetRequest;
import com.trkpo.ptinder.utils.PetInfoUtils;

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
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
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
    private ArrayAdapter<String> addressAdapter;
    private ArrayAdapter<String> typeAdapter;

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

        addressAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);
        addressSpinner.setAdapter(addressAdapter);

        typeAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);


        favouritePetsId = new ArrayList<>();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null) {
            loadFavouriteId(signInAccount.getId());
            setAddresses();
            setTypes();
        }

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
                if (signInAccount != null) {
                    showResults(signInAccount.getId());
                }
            }
        });

        return root;
    }

    public void showResults(String googleId, String ... optUrl) {
        /* Variable for test cases*/
        boolean connectionPermission = optUrl.length != 2 || Boolean.parseBoolean(optUrl[1]);
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        if (activity != null) {
            String address = "" + addressSpinner.getSelectedItem();
            String gender = translateGender(radioGroup);
            String purpose = PetInfoUtils.formatPurposeToEnum("" + purposeSpinner.getSelectedItem());
            String type = "" + (typeSpinner.getSelectedItem() != null ? typeSpinner.getSelectedItem() : "");
            petCardAdapter.clearItems();

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
            url = optUrl.length == 0 ? url : optUrl[0];

            try {
                String response = new GetRequest().execute(url).get();
                Log.i("INFO", "GOT " + response);
                petCardAdapter.setItems(PetInfoUtils.getPetsFromJSON(response, null, googleId, 2));
            } catch (ExecutionException | InterruptedException | JSONException error) {
                error.printStackTrace();
            }
        }
    }

    public void setAddresses(String ... optUrl) {
        /* Variable for test cases*/
        boolean connectionPermission = optUrl.length != 2 || Boolean.parseBoolean(optUrl[1]);
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String url = optUrl.length == 0 ? SEARCH_PATH + "/address" : optUrl[0];
        try {
            String response = new GetRequest().execute(url).get();
            Log.i("INFO", "GOT " + response);
            addressAdapter.addAll(getAddrFromJSON(response));
        } catch (ExecutionException | InterruptedException error) {
            error.printStackTrace();
        }
    }

    public void setTypes(String ... optUrl) {
        /* Variable for test cases*/
        boolean connectionPermission = optUrl.length != 2 || Boolean.parseBoolean(optUrl[1]);
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String url = optUrl.length == 0 ? SEARCH_PATH + "/types" : optUrl[0];
        try {
            String response = new GetRequest().execute(url).get();
            Log.i("INFO", "GOT " + response);
            typeAdapter.addAll(getTypesFromJSON(response));
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
        }

        typeSpinner.setAdapter(typeAdapter);
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

    public String translateGender(RadioGroup gender) {
        RadioButton myRadioButton = root.findViewById(gender.getCheckedRadioButtonId());
        if (myRadioButton != null) {
            return myRadioButton.getText().equals("Мужской") ? "MALE" : "FEMALE";
        } else {
            return "";
        }
    }

    private String form(String animalType) {
        return animalType != null ? StringUtils.substringBetween(animalType, "\"type\":\"", "\"") : "";
    }

    public Collection<String> getAddrFromJSON(String response) {
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

    public void loadFavouriteId(String googleId, String ... optUrl) {
        /* Variable for test cases*/
        boolean connectionPermission = optUrl.length != 2 || Boolean.parseBoolean(optUrl[1]);
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl.length == 0 ? FAVOURITE_PATH + "/user/id/" + googleId : optUrl[0];
        try {
            String response = new GetRequest().execute(url).get();
            JSONArray responseArray = new JSONArray(response);
            Log.i("INFO", "GOT " + response);
            for (int i = 0; i < responseArray.length(); i++) {
                favouritePetsId.add(Long.valueOf(responseArray.get(i).toString()));
            }
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
        }
    }

    public ArrayAdapter<String> getAddressAdapter() {
        return addressAdapter;
    }

    public PetCardAdapter getPetCardAdapter() {
        return petCardAdapter;
    }

    public ArrayAdapter<String> getTypeAdapter() {
        return typeAdapter;
    }

    public List<Long> getFavouritePetsId() {
        return favouritePetsId;
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }
}