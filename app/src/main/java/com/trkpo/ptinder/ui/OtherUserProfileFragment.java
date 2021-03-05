package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.config.PhotoTask;
import com.trkpo.ptinder.HTTP.Connection;
import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.utils.PetInfoUtils;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.CONTACT_PATH;
import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.SUBSCRIPTION_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class OtherUserProfileFragment extends Fragment {

    private Activity activity;
    private View root;

    private ImageView userIcon;
    private ImageView userSubscribe;
    private ImageView requestUserContacts;
    private TextView username;
    private TextView location;
    private String userImageUrl;
    private TextView phone;
    private TextView email;
    private RelativeLayout phoneLayout;
    private RelativeLayout emailLayout;
    private RecyclerView petCardRecycleView;
    private PetCardAdapter petCardAdapter;

    private boolean isSubscr;

    /* Variable for testing*/
    private String optUrl;
    private boolean connectionPermission;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_other_user_profile, container, false);
        activity = getActivity();
        userIcon = root.findViewById(R.id.user_icon);
        username = root.findViewById(R.id.username);
        location = root.findViewById(R.id.location);
        phone = root.findViewById(R.id.user_phone);
        email = root.findViewById(R.id.user_email);
        phoneLayout = root.findViewById(R.id.other_user_profile_phone_layout);
        emailLayout = root.findViewById(R.id.other_user_profile_email_layout);
        userSubscribe = root.findViewById(R.id.user_subscribe);
        requestUserContacts = root.findViewById(R.id.request_for_user_contacts);
        optUrl = null;
        connectionPermission = true;

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        String currentUserGoogleId = signInAccount != null ? signInAccount.getId() : null;
        String userGoogleId = (String) (getArguments() != null ? getArguments().getString("googleId") : null);

        initUserInfo(currentUserGoogleId, userGoogleId);

        userSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSubscr) {
                    subscribeOnUser(currentUserGoogleId, userGoogleId);
                } else {
                    if (activity != null) {
                        unsubscribeOnUser(currentUserGoogleId, userGoogleId);
                    }
                }
            }
        });

        requestUserContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestUserContacts(currentUserGoogleId, userGoogleId);
            }
        });

        initRecycleView(currentUserGoogleId, userGoogleId);

        return root;
    }

    public void initUserInfo(String currentUserGoogleId, String userGoogleId) {
        if (currentUserGoogleId != null && userGoogleId != null) {
            checkSubscription(currentUserGoogleId, userGoogleId);
            showInfo(currentUserGoogleId, userGoogleId);
        }
    }

    private void initRecycleView(String currentUserGoogleId, String userGoogleId) {
        petCardRecycleView = root.findViewById(R.id.pet_cards_recycle_view);
        petCardRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        petCardAdapter = new PetCardAdapter();
        petCardRecycleView.setAdapter(petCardAdapter);

        if (currentUserGoogleId != null && userGoogleId != null)
            loadFavouriteId(currentUserGoogleId, userGoogleId);
    }

    public void requestUserContacts(String currentUserGoogleId, String userGoogleId) {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl == null ? CONTACT_PATH + "/request/" + currentUserGoogleId + "/" + userGoogleId : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            String response = new PostRequest().execute(new PostRequestParams(url, null)).get();
            if (!response.equals("")) Log.d("VOLLEY", "Success response (request user info) from " + currentUserGoogleId + " to " + userGoogleId);
        } catch (ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", "Not Success response (request user info): " + error.toString());
        }
    }

    public void subscribeOnUser(String currentUserGoogleId, String userGoogleId) {
        if (!Connection.hasConnection(activity) | !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl == null ? SUBSCRIPTION_PATH + "/" + currentUserGoogleId : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("googleId", userGoogleId);
            String response = new PostRequest().execute(new PostRequestParams(url, requestObject.toString())).get();
            if (!response.equals("")) {
                Log.i("SUBSCRIPTION", "Successfully subscribed on user " + userGoogleId);
                isSubscr = true;
            }
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.e("VOLLEY", error.toString());
        }
        userSubscribe.setColorFilter(userIcon.getContext().getResources().getColor(R.color.colorIsSubscribed));
        isSubscr = true;
    }

    public void unsubscribeOnUser(String currentUserGoogleId, String userGoogleId) {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl == null ? SUBSCRIPTION_PATH + "/" + currentUserGoogleId + "/" + userGoogleId : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            String response = new DeleteRequest().execute(url).get();
            if (!response.equals("")) {
                Log.i("SUBSCRIPTION", "Successfully unsubscribed from user " + userGoogleId);
                isSubscr = false;
            }
        } catch (ExecutionException | InterruptedException error) {
            Log.e("VOLLEY", error.toString());
        }

        isSubscr = false;
        userSubscribe.setColorFilter(userIcon.getContext().getResources().getColor(R.color.colorNotFavourite));
    }

    public void checkSubscription(String currentUserGoogleId, String userGoogleId) {
        if (activity != null) {
            if (!Connection.hasConnection(activity) || !connectionPermission) {
                Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
                return;
            }

            String url = optUrl == null ? SUBSCRIPTION_PATH + "/check/" + currentUserGoogleId + "/" + userGoogleId : optUrl;
            if (optUrl != null) resetOptUrlAndConnectionPermission();
            try {
                String response = new GetRequest().execute(url).get();
                isSubscr = response.contains("true");
                if (isSubscr) {
                    userSubscribe.setColorFilter(userIcon.getContext().getResources().getColor(R.color.colorIsSubscribed));
                } else {
                    userSubscribe.setColorFilter(userIcon.getContext().getResources().getColor(R.color.colorNotFavourite));
                }
            } catch (ExecutionException | InterruptedException error) {
                Log.e("VOLLEY", "Making get request (load pets): request error - " + error.toString());
            }
        }
    }

    public void showInfo(String currentUserGoogleId, String userGoogleId) {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl == null ? USERS_PATH + "/" + userGoogleId : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            String stringJsonResponse = new GetRequest().execute(url).get();
            JSONObject jsonResponse = new JSONObject(stringJsonResponse);
            username.setText(jsonResponse.getString("firstName") + " " + jsonResponse.getString("lastName"));
            location.setText(jsonResponse.getString("address"));
            String photoUrl = jsonResponse.getString("photoUrl");
            if (!photoUrl.equals("")) {
                userIcon.setImageBitmap(new PhotoTask().execute(photoUrl).get());
            }
            boolean isContactInfoPublic = jsonResponse.getBoolean("contactInfoPublic");
            if (isContactInfoPublic) {
                email.setText(jsonResponse.getString("email"));
                if (!jsonResponse.getString("number").equals("")) {
                    phone.setText(jsonResponse.getString("number"));
                } else {
                    phone.setText("-");
                }
                emailLayout.setVisibility(View.VISIBLE);
                phoneLayout.setVisibility(View.VISIBLE);
            }
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.e("VOLLEY", "Making get request (get user by google id): request error - " + error.toString());
        }
    }

    public void loadFavouriteId(String currentUserGoogleId, String userGoogleId) {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl == null ? FAVOURITE_PATH + "/user/id/" + currentUserGoogleId : optUrl;
        try {
            String stringResponse = new GetRequest().execute(url).get();
            Log.d("VOLLEY", "Making get request (load favourite pets id): response - " + stringResponse);
            JSONArray response = new JSONArray(stringResponse);
            List<Long> favouritePetsId = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                favouritePetsId.add(Long.valueOf(response.get(i).toString()));
            }
            loadPets(favouritePetsId, currentUserGoogleId, userGoogleId);
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.e("VOLLEY", "Making get request (load favourite pets id): error - " + error.toString());
        }
        if (optUrl != null) resetOptUrlAndConnectionPermission();
    }

    public void loadPets(final List<Long> favouritePetsId, String currentUserGoogleId, String userGoogleId) {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl == null ? PETS_PATH + "/owner/" + userGoogleId : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            String response = new GetRequest().execute(url).get();
            Log.d("VOLLEY", "Making get request (load pets): response - " + response);
            petCardAdapter.setItems(PetInfoUtils.getPetsFromJSON(response, favouritePetsId, currentUserGoogleId, 4));
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.e("VOLLEY", "Making get request (load pets): error - " + error.toString());
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

    public boolean isSubscr() {
        return isSubscr;
    }

    public PetCardAdapter getPetCardAdapter() {
        return petCardAdapter;
    }

    public ImageView getUserSubscribe() {
        return userSubscribe;
    }

    public ImageView getRequestUserContacts() {
        return requestUserContacts;
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
