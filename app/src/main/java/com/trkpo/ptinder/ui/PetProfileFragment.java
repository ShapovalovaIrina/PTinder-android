package com.trkpo.ptinder.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.config.PhotoTask;
import com.trkpo.ptinder.pojo.PetInfo;
import com.trkpo.ptinder.HTTP.Connection;

import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;

public class PetProfileFragment extends Fragment {
    private PetInfo petInfo;
    private FloatingActionButton favourite;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pet_profile, container, false);

        petInfo = (PetInfo) getArguments().getSerializable("petInfo");

        CarouselView petIcons = root.findViewById(R.id.carousel_view);
        TextView petName = root.findViewById(R.id.full_pet_info_name);
        TextView petType = root.findViewById(R.id.full_pet_info_type);
        TextView petBreed = root.findViewById(R.id.full_pet_info_breed);
        TextView petAge = root.findViewById(R.id.full_pet_info_age);
        TextView petPurpose = root.findViewById(R.id.full_pet_info_purpose);
        TextView petComment = root.findViewById(R.id.full_pet_info_comment);
        ImageView petGender = root.findViewById(R.id.full_pet_info_gender);
        favourite = root.findViewById(R.id.full_pet_info_favourite);

        if (petInfo.getIconsAmount() > 0) {
            petIcons.setPageCount(petInfo.getIconsAmount());
            petIcons.setImageListener(new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageBitmap(petInfo.getIcon(position));
                }
            });
        } else {
            /* Set default image */
            petIcons.setPageCount(1);
            petIcons.setImageListener(new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageResource(R.drawable.cat);
                }
            });
        }
        if (petInfo.getGender().equals("MALE"))
            petGender.setImageResource(R.drawable.male);
        else if (petInfo.getGender().equals("FEMALE"))
            petGender.setImageResource(R.drawable.female);
        else
            petGender.setVisibility(View.INVISIBLE);
        petName.setText(petInfo.getName());
        petType.setText(petInfo.getAnimalType());
        petBreed.setText(petInfo.getBreed());
        petAge.setText(petInfo.getAge());
        petPurpose.setText(petInfo.getPurpose());
        petComment.setText(petInfo.getComment());

        ImageView ownerIcon = root.findViewById(R.id.full_pet_info_owner_icon);
        TextView ownerName = root.findViewById(R.id.full_pet_info_owner_name);
        ownerName.setText(petInfo.getOwnerName());
        ownerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("googleId", petInfo.getOwnerId());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_nav_pet_profile_to_nav_other_user_profile, bundle);
            }
        });

        setFavouriteColor();
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

        try {
            ownerIcon.setImageBitmap(new PhotoTask().execute(petInfo.getOwnerIconURL()).get());
        } catch (ExecutionException | InterruptedException e) {
            Log.e("BITMAP", "Got error during bitmap parsing" + e.toString());
        }

        return root;
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
                Log.d("VOLLEY", "Not Success response (add to favourite): " + error.toString());
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
                Log.d("VOLLEY", "Not Success response (delete from favourite): " + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void setFavouriteColor() {
        if (petInfo.isFavourite()) {
            favourite.setColorFilter(favourite.getContext().getResources().getColor(R.color.colorIsFavourite));
        } else {
            favourite.setColorFilter(favourite.getContext().getResources().getColor(R.color.colorNotFavourite));
        }
    }
}
