package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.os.Bundle;
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
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.pojo.PetInfo;

import java.util.Arrays;
import java.util.Collection;

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
            String url = "http://192.168.0.102:8080/ptinder/users/google/" + googleId;

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
        Collection<PetInfo> pets = getPets();
        petCardAdapter.setItems(pets);
    }

    private Collection<PetInfo> getPets(){
        return Arrays.asList(
                new PetInfo("Симба", "Котик :3", "1 год"),
                new PetInfo("Мотя", "Котик :3", "2 года"),
                new PetInfo("Рэя", "Котик :3", "3 года")
        );
    }
}
