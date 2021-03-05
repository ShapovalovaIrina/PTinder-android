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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.trkpo.ptinder.HTTP.Connection;
import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.HTTP.PutRequest;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.activity.LoginActivity;
import com.trkpo.ptinder.adapter.SmallPetAdapter;
import com.trkpo.ptinder.pojo.PetInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class SettingsFragment extends Fragment {
    public Activity activity;
    public View root;

    public TextView firstName;
    public TextView lastName;
    public TextView location;
    public TextView phone;
    public TextView email;
    public RadioGroup gender;
    public Button updateUser;
    public Button deleteUser;
    public Button logoutUser;

    public RecyclerView smallPetRecycleView;
    public SmallPetAdapter smallPetAdapter;

    public String googleId;
    private String optUrl;
    private boolean connectionPermission;

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
                    updateUser();
                }
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
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

    public void updateUser() {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        String url = optUrl == null ? USERS_PATH + "/" + googleId : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstName", firstName.getText());
            jsonObject.put("lastName", lastName.getText());
            jsonObject.put("gender", gender.getCheckedRadioButtonId() == 0 ? "FEMALE" : "MALE");
            jsonObject.put("email", email.getText());
            jsonObject.put("number", phone.getText());
            jsonObject.put("address", location.getText());
            String response = new PutRequest().execute(new PostRequestParams(url, jsonObject.toString()))
                    .get();
            if (!response.equals("")) {
                Log.d("VOLLEY", "Success response (update user). ");
            }

        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser() {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        String url = optUrl == null ? USERS_PATH + "/" + googleId : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            String response = new DeleteRequest().execute(url).get();
            if (!response.equals("")) {
                Log.d("VOLLEY", "Success response (delete user)");
            }
        } catch (ExecutionException | InterruptedException error) {
            Log.d("VOLLEY", "Not Success response (delete from favourite): " + error.toString());
        }
    }

    public void signOut() {
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

    public void initUserInfo() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null) {
            showInfo(signInAccount.getId());
            googleId = signInAccount.getId();
        }
    }

    public void initRecycleView() {
        smallPetRecycleView = root.findViewById(R.id.small_pet_cards_recycle_view);
        smallPetRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        smallPetAdapter = new SmallPetAdapter();
        smallPetRecycleView.setAdapter(smallPetAdapter);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null) {
            loadPets();
        }
    }

    public void showInfo(String googleId, String ... optUrl ) {
        boolean connectionPermission = optUrl.length != 2 || Boolean.parseBoolean(optUrl[1]);
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        String url = optUrl.length == 0 ? USERS_PATH + "/" + googleId : optUrl[0];
        try {
            String response = new GetRequest().execute(url).get();
            JSONObject jsonResponse = new JSONObject(response);
            Log.d("VOLLEY", "Get info: " + jsonResponse);
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
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
            Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadPets(String ... optUrl) {
        boolean connectionPermission = optUrl.length != 2 || Boolean.parseBoolean(optUrl[1]);
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        String url = optUrl.length == 0 ? PETS_PATH + "/owner/" + googleId : optUrl[0];
        try {
            String response = new GetRequest().execute(url).get();
            Log.d("VOLLEY", "Get news: " + response);
            smallPetAdapter.setItems(getPetsFromJSON(response));
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
            Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public Collection<PetInfo> getPetsFromJSON(String jsonString) throws JSONException {
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

    public void setOptUrlAndConnectionPermission(String optUrl, boolean connectionPermission) {
        this.optUrl = optUrl;
        this.connectionPermission = connectionPermission;
    }

    public void resetOptUrlAndConnectionPermission() {
        optUrl = null;
        connectionPermission = true;
    }

}
