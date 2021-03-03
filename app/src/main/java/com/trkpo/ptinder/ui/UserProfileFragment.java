package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.os.Bundle;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.config.PhotoTask;
import com.trkpo.ptinder.HTTP.Connection;
import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private ImageView settingsIcon;
    private TextView username;
    private TextView location;
    private TextView phone;
    private TextView email;
    private RecyclerView petCardRecycleView;
    private PetCardAdapter petCardAdapter;
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
        settingsIcon = root.findViewById(R.id.settings_icon);

        addPetBtn = root.findViewById(R.id.add_pet);
        addPetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.nav_pet_registration);
            }
        });


        try {
            if (USER_ICON_URL != null)
                userIcon.setImageBitmap(new PhotoTask().execute(USER_ICON_URL).get());
        } catch (ExecutionException | InterruptedException e) {
            Log.e("BITMAP", "Got error during bitmap parsing" + e.toString());
        }
        initUserInfo();
        initRecycleView();

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.nav_settings);
            }
        });

        return root;
    }

    private void initUserInfo() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null) {
            showInfo(signInAccount.getId());
        }
    }

    public void showInfo(String googleId, String ... optUrl) {
        /* Variable for test cases*/
        boolean connectionPermission = optUrl.length == 2 ? Boolean.valueOf(optUrl[1]) : true;
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl.length == 0 ? USERS_PATH + "/" + googleId : optUrl[0];
        try {
            String response = new GetRequest().execute(url).get();
            JSONObject jsonResponse = new JSONObject(response);
            Log.d("Logs", "Get user info. Url " + url);
            String name = jsonResponse.getString("firstName") + " " + jsonResponse.getString("lastName");
            username.setText(name);
            location.setText(jsonResponse.getString("address"));
            email.setText(jsonResponse.getString("email"));
            if (!jsonResponse.getString("number").equals("")) {
                phone.setText(jsonResponse.getString("number"));
            } else {
                phone.setText("-");
            }
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
        }
    }

    private void initRecycleView() {
        petCardRecycleView = root.findViewById(R.id.pet_cards_recycle_view);
        petCardRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        petCardAdapter = new PetCardAdapter();
        petCardRecycleView.setAdapter(petCardAdapter);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null) {
            loadFavouriteId(signInAccount.getId());
        }
    }

    public void loadFavouriteId(String googleId, String ... optUrl) {
        /* Variable for test cases*/
        boolean connectionPermission = optUrl.length == 2 ? Boolean.valueOf(optUrl[1]) : true;
        if (!Connection.hasConnection(activity) ||!connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl.length == 0 ? FAVOURITE_PATH + "/user/id/" + googleId : optUrl[0];
        try {
            String stringResponse = new GetRequest().execute(url).get();
            JSONArray response = new JSONArray(stringResponse);
            Log.d("VOLLEY", "Making get request (load favourite pets id): response - " + response.toString());
            List<Long> favouritePetsId = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                favouritePetsId.add(Long.valueOf(response.get(i).toString()));
            }
            loadPets(googleId, favouritePetsId, optUrl);
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
            Toast.makeText(activity, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadPets(String googleId, List<Long> favouritePetsId, String ... optUrl) {
        /* Variable for test cases*/
        boolean connectionPermission = optUrl.length == 2 ? Boolean.valueOf(optUrl[1]) : true;
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl.length == 0 ? PETS_PATH + "/owner/" + googleId : optUrl[0];
        Log.d("Logs", "Get user pets. Url " + url);
        try {
            String response = new GetRequest().execute(url).get();
            Log.d("VOLLEY", "Making get request (load pets): response - " + response);
            petCardAdapter.setItems(PetInfoUtils.getPetsFromJSON(response, favouritePetsId, googleId, 1));
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
            Toast.makeText(activity, "JSON exception: " + error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getUsername() {
        return username.getText().toString();
    }

    public String getLocation() {
        return location.getText().toString();
    }

    public String getPhone() {
        return phone.getText().toString();
    }

    public String getEmail() {
        return email.getText().toString();
    }

    public PetCardAdapter getPetCardAdapter() {
        return petCardAdapter;
    }
}
