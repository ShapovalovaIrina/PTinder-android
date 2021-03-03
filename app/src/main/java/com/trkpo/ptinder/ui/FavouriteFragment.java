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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.HTTP.Connection;
import com.trkpo.ptinder.utils.PetInfoUtils;
import com.trkpo.ptinder.HTTP.GetRequest;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;

public class FavouriteFragment extends Fragment {
    private View root;
    private Activity activity;
    private RecyclerView petCardRecycleView;
    private PetCardAdapter petCardAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_favourite, container, false);
        activity = getActivity();

        initRecycleView();
        return root;
    }

    private void initRecycleView() {
        petCardRecycleView = root.findViewById(R.id.pet_cards_recycle_view);
        petCardRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        petCardAdapter = new PetCardAdapter();
        petCardRecycleView.setAdapter(petCardAdapter);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null) {
            loadPets(signInAccount.getId());
        }
    }

    public void loadPets(String googleId, String ... optUrl) {
        /* Variable for test cases*/
        boolean connectionPermission = optUrl.length == 2 ? Boolean.valueOf(optUrl[1]) : true;
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        String url = optUrl.length == 0 ? FAVOURITE_PATH + "/user/full/" + googleId : optUrl[0];
        try {
            String response = new GetRequest().execute(url).get();
            Log.d("VOLLEY", "Making get request (load pets): response - " + response.toString());
            petCardAdapter.setItems(PetInfoUtils.getPetsFromJSON(response, null, googleId, 3));
            petCardAdapter.setFavouriteFragment(true);
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
            Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public PetCardAdapter getPetCardAdapter() {
        return petCardAdapter;
    }
}