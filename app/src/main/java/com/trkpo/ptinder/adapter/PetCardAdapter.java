package com.trkpo.ptinder.adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.activity.NavigationActivity;
import com.trkpo.ptinder.pojo.PetInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class PetCardAdapter extends RecyclerView.Adapter<PetCardAdapter.ViewHolder> {
    private List<PetInfo> petsList = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PetInfo petInfo;
        private TextView petName;
        private TextView petBreed;
        private TextView petAge;
        private ImageView petImage;
        private ImageView favourite;

        public ViewHolder(View itemView) {
            super(itemView);
            this.petName = itemView.findViewById(R.id.card_pet_name);
            this.petBreed = itemView.findViewById(R.id.card_pet_breed);
            this.petAge = itemView.findViewById(R.id.card_pet_age);
            this.petImage = itemView.findViewById(R.id.card_pet_image);
            this.favourite = itemView.findViewById(R.id.card_pet_is_favourite);

            petImage.setOnClickListener(this);
            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (petInfo.isFavourite()) {
                        deleteFromFavourite(view);
                    } else {
                        addToFavourite(view);
                    }
                }
            });
        }

        public void bind(PetInfo petInfo) {
            this.petInfo = petInfo;
            petName.setText(petInfo.getName() != null ? petInfo.getName() : "");
            petBreed.setText(petInfo.getAnimalType() != null ? petInfo.getAnimalType() : "");
            petAge.setText(petInfo.getAge() != null ? petInfo.getAge() : "");
            if (petInfo.getIconsAmount() != 0) {
                /* Set first image as main */
                petImage.setImageBitmap(petInfo.getIcons().get(0));
            }
            setFavouriteColor();
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("petInfo", petInfo);
            NavController navController = Navigation.findNavController(v);
            if (petInfo.getDirection() == 1) {
                navController.navigate(R.id.action_nav_user_profile_to_nav_pet_profile, bundle);
            }
            if (petInfo.getDirection() == 2) {
                navController.navigate(R.id.action_nav_search_to_nav_pet_profile, bundle);
            }
        }

        private void addToFavourite(View view) {
            RequestQueue queue = Volley.newRequestQueue(view.getContext());
            String url = FAVOURITE_PATH + "/" + petInfo.getId();

            JSONObject postData = new JSONObject();
            try {
                postData.put("googleId", petInfo.getOwnerId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("VOLLEY", "Success response (add to favourite): " + response);
                            petInfo.setFavourite(true);
                            setFavouriteColor();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VOLLEY", "Not Success response (add to favourite): " + error.toString());
                }
            });
            queue.add(jsonObjectRequest);
        }

        private void deleteFromFavourite(View view) {
            RequestQueue queue = Volley.newRequestQueue(view.getContext());
            String url = FAVOURITE_PATH + "/" + petInfo.getId();

            JSONObject postData = new JSONObject();
            try {
                postData.put("googleId", petInfo.getOwnerId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, postData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("VOLLEY", "Success response (delete from favourite): " + response);
                            petInfo.setFavourite(false);
                            setFavouriteColor();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VOLLEY", "Not Success response (delete from favourite): " + error.toString());
                }
            });
            queue.add(jsonObjectRequest);
        }

        private void setFavouriteColor() {
            if (petInfo.isFavourite()) {
                favourite.setColorFilter(petImage.getContext().getResources().getColor(R.color.colorIsFavourite));
            } else {
                favourite.setColorFilter(petImage.getContext().getResources().getColor(R.color.colorNotFavourite));
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(petsList.get(position));
    }

    @Override
    public int getItemCount() {
        return petsList.size();
    }

    public void setItems(Collection<PetInfo> pets) {
        petsList.addAll(pets);
        notifyDataSetChanged();
    }

    public void clearItems() {
        petsList.clear();
        notifyDataSetChanged();
    }
}
