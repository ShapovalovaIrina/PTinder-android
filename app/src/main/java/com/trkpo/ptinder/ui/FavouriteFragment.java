package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.os.Bundle;
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
import com.trkpo.ptinder.utils.Connection;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONException;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;

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
        if (!Connection.hasConnection(activity)) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = FAVOURITE_PATH + "/user/full/" + googleId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("VOLLEY", "Making get request (load pets): response - " + response.toString());
                            petCardAdapter.setItems(PetInfoUtils.getPetsFromJSON(response, null, googleId, 3));
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
}