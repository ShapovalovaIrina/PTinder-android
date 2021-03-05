package com.trkpo.ptinder.ui;

import android.app.Activity;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.trkpo.ptinder.HTTP.Connection;
import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.config.PhotoTask;
import com.trkpo.ptinder.pojo.PetInfo;

import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;

public class PetProfileFragment extends Fragment {
    private Activity activity;
    private PetInfo petInfo;
    private FloatingActionButton favourite;
    private TextView petName;
    private TextView petType;
    private TextView petBreed;
    private TextView petAge;
    private TextView petPurpose;
    private TextView petComment;
    private ImageView petGender;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_pet_profile, container, false);
        activity = getActivity();

        petInfo = (PetInfo) getArguments().getSerializable("petInfo");

        CarouselView petIcons = root.findViewById(R.id.carousel_view);
        petName = root.findViewById(R.id.full_pet_info_name);
        petType = root.findViewById(R.id.full_pet_info_type);
        petBreed = root.findViewById(R.id.full_pet_info_breed);
        petAge = root.findViewById(R.id.full_pet_info_age);
        petPurpose = root.findViewById(R.id.full_pet_info_purpose);
        petComment = root.findViewById(R.id.full_pet_info_comment);
        petGender = root.findViewById(R.id.full_pet_info_gender);
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
                    deleteFromFavourite();
                } else {
                    addToFavourite();
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

    public void addToFavourite(String ... optUrl) {
        boolean connectionPermission = optUrl.length != 2 || Boolean.parseBoolean(optUrl[1]);
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl.length == 0 ? FAVOURITE_PATH + "/" + petInfo.getId() + "/user/" + petInfo.getCurrentUserId() : optUrl[0];
        try {
            String response = new PostRequest().execute(new PostRequestParams(url, "")).get();
            if (!response.equals("")) {
                Log.d("VOLLEY", "Success response (add to favourite). " +
                        "Pet id: " + petInfo.getId() + ", user id: " + petInfo.getCurrentUserId());
                petInfo.setFavourite(true);
                setFavouriteColor();
            }
        } catch (ExecutionException | InterruptedException error) {
            Log.d("VOLLEY", "Not Success response (add to favourite): " + error.toString());
        }
    }

    public void deleteFromFavourite(String ... optUrl) {
        boolean connectionPermission = optUrl.length != 2 || Boolean.parseBoolean(optUrl[1]);
        if (!Connection.hasConnection(activity) | !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl.length == 0 ? FAVOURITE_PATH + "/" + petInfo.getId() + "/user/" + petInfo.getCurrentUserId() : optUrl[0];
        try {
            String response = new DeleteRequest().execute(url).get();
            if (!response.equals("")) {
                Log.d("VOLLEY", "Success response (delete from favourite)" +
                        "Pet id: " + petInfo.getId() + ", user id: " + petInfo.getCurrentUserId());
                petInfo.setFavourite(false);
                setFavouriteColor();
            }
        } catch (ExecutionException | InterruptedException error) {
            Log.d("VOLLEY", "Not Success response (delete from favourite): " + error.toString());
        }
    }

    private void setFavouriteColor() {
        if (petInfo.isFavourite()) {
            favourite.setColorFilter(favourite.getContext().getResources().getColor(R.color.colorIsFavourite));
        } else {
            favourite.setColorFilter(favourite.getContext().getResources().getColor(R.color.colorNotFavourite));
        }
    }

    public TextView getPetName() {
        return petName;
    }

    public TextView getPetType() {
        return petType;
    }

    public TextView getPetBreed() {
        return petBreed;
    }

    public TextView getPetAge() {
        return petAge;
    }

    public TextView getPetPurpose() {
        return petPurpose;
    }

    public TextView getPetComment() {
        return petComment;
    }
}
