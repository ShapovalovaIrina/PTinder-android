package com.trkpo.ptinder.adapter;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.config.PhotoTask;
import com.trkpo.ptinder.pojo.PetInfo;
import com.trkpo.ptinder.utils.Connection;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;
import static com.trkpo.ptinder.config.Constants.USER_ICON_URL;

public class PetCardAdapter extends RecyclerView.Adapter<PetCardAdapter.ViewHolder> {
    private List<PetInfo> petsList = new ArrayList<>();
    private boolean isFavouriteFragment = false;

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
                    if (petInfo.isFavourite() && isFavouriteFragment) {
                        deletePetById(petInfo.getId());
                    }

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
            if (petInfo.getDirection() == 3) {
                navController.navigate(R.id.action_nav_favourite_to_nav_pet_profile, bundle);
            }
            if (petInfo.getDirection() == 4) {
                navController.navigate(R.id.action_nav_other_user_profile_to_nav_pet_profile, bundle);
            }
        }

        private void addToFavourite(View view) {
            if (!Connection.hasConnection(view.getContext())) {
                Toast.makeText(view.getContext(), "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
                return;
            }
            RequestQueue queue = Volley.newRequestQueue(view.getContext());
            String url = FAVOURITE_PATH + "/" + petInfo.getId() + "/user/" + petInfo.getCurrentUserId();

            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("VOLLEY", "Success response (add to favourite). " +
                                    "Pet id: " + petInfo.getId() + ", user id: " + petInfo.getCurrentUserId());
                            petInfo.setFavourite(true);
                            setFavouriteColor();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", "Not Success response (add to favourite): " + error.toString());
                }
            });
            queue.add(jsonObjectRequest);
        }

        private void deleteFromFavourite(View view) {
            if (!Connection.hasConnection(view.getContext())) {
                Toast.makeText(view.getContext(), "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
                return;
            }
            RequestQueue queue = Volley.newRequestQueue(view.getContext());
            String url = FAVOURITE_PATH + "/" + petInfo.getId() + "/user/" + petInfo.getCurrentUserId();

            StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("VOLLEY", "Success response (delete from favourite)" +
                                    "Pet id: " + petInfo.getId() + ", user id: " + petInfo.getCurrentUserId());
                            petInfo.setFavourite(false);
                            setFavouriteColor();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", "Not Success response (delete from favourite): " + error.toString());
                }
            });
            queue.add(jsonObjectRequest);
        }

        private void setFavouriteColor() {
            if (petInfo.isFavourite() || isFavouriteFragment) {
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

    public void setFavouriteFragment(boolean isFavouriteFragment) {
        this.isFavouriteFragment = isFavouriteFragment;
    }

    public void deletePetById(Long petId) {
        int position = -1;
        for (int i = 0; i < petsList.size(); i++) {
            if (petsList.get(i).getId().equals(petId)) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            petsList.remove(position);
            notifyDataSetChanged();
        }
    }
}
