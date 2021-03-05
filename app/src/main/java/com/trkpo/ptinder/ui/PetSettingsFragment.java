package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;
import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.HTTP.PutRequest;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.PetInfo;
import com.trkpo.ptinder.HTTP.Connection;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;

public class PetSettingsFragment extends Fragment {
    private Activity activity;
    private View root;

    private TextView petName;
    private TextView petAge;
    private TextView petBreed;
    private TextView petComment;
    private Spinner petType;
    private Spinner petPurpose;
    private String googleId;
    private RadioGroup rgGender;
    private Button updatePet;
    private Button deletePet;

    private PetInfo petInfo;

    private CarouselView petImages;

    private List<Bitmap> imagesBitmap = new ArrayList<>();

    static final int GALLERY_REQUEST = 1;

    /* Variable for testing*/
    private String optUrl;
    private boolean connectionPermission;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_pet_settings, container, false);
        activity = getActivity();

        if (getArguments() != null) loadPet((Long) getArguments().getSerializable("petId"));
        petName = root.findViewById(R.id.pet_name);
        petAge = root.findViewById(R.id.pet_age);
        petBreed = root.findViewById(R.id.pet_breed);
        petComment = root.findViewById(R.id.comment);
        rgGender = root.findViewById(R.id.radio_group_gender_pet);
        petType = root.findViewById(R.id.type_spinner);
        petPurpose = root.findViewById(R.id.purpose_spinner);
        updatePet = root.findViewById(R.id.update_pet);
        deletePet = root.findViewById(R.id.delete_pet);
        optUrl = null;
        connectionPermission = true;

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        googleId = signInAccount != null ? signInAccount.getId() : null;

        petImages = root.findViewById(R.id.carousel_view);
        petImages.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                imageView.setImageResource(R.drawable.no_photo);
            }
        });
        petImages.setPageCount(1);
        petImages.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                performOnImageClickListener();
            }
        });

        updatePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePet(petInfo.getId());
            }
        });

        deletePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePet(petInfo.getId());
            }
        });

        return root;
    }

    public void performOnImageClickListener() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(photoPickerIntent, "ChooseFile"), GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == GALLERY_REQUEST) && (resultCode == RESULT_OK)) {
            if (data != null) {
                imagesBitmap.clear();
                if (data.getClipData() != null) {
                    if (data.getClipData().getItemCount() > 10) {
                        Toast.makeText(activity, "You can not choose more than 10 images", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            loadImage(data.getClipData().getItemAt(i).getUri());
                        }
                    }
                } else {
                    loadImage(data.getData());
                }
            }
        }
    }

    private void loadImage(Uri selectedImg) {
        Glide.with(this)
                .downloadOnly()
                .load(selectedImg)
                .into(new CustomTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, Transition<? super File> transition) {
                        long sizeInMb = resource.length() / 1024 / 1024;
                        if (sizeInMb > 5) {
                            Toast.makeText(activity, "Image size should not be more than 5 Mb", Toast.LENGTH_SHORT).show();
                        } else {
                            imagesBitmap.add(BitmapFactory.decodeFile(resource.getPath()));
                            petImages.setPageCount(imagesBitmap.size());
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    public void loadPet(Long petId) {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl == null ? PETS_PATH + "/" + petId : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            String response = new GetRequest().execute(url).get();
            Log.d("VOLLEY", "Making get request (load pet): response - " + response);
            petInfo = getPetsFromJSON(response);
            showPetInfo();
        } catch (ExecutionException | InterruptedException | JSONException error) {
            Log.e("VOLLEY", "Making get request (load pet): error - " + error.toString());
        }
    }

    public void deletePet(Long petId) {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl == null ? PETS_PATH + "/" + petId : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            String response = new DeleteRequest().execute(url).get();
            Log.i("volley", response);
            Toast.makeText(getContext(), "Питомец был успешно удален.", Toast.LENGTH_LONG).show();
            NavController navController = Navigation.findNavController(root);
            navController.navigateUp();
        } catch (ExecutionException | InterruptedException | IllegalStateException error) {
            Log.e("VOLLEY", "Making delete request (delete pet): error - " + error.toString());
        }
    }

    public void updatePet(Long petId) {
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject requestObject = PetInfoUtils.setPetToJSON(
                petName.getText().toString(),
                petAge.getText().toString(),
                rgGender.getCheckedRadioButtonId() == 0 ? "FEMALE" : "MALE",
//                petType.getSelectedItem().toString(),
                "Dog",
                petBreed.getText().toString(),
//                petPurpose.getSelectedItem().toString(),
                "Walking",
                petComment.getText().toString(),
                imagesBitmap,
                googleId
        );
        final String requestBody = requestObject.toString();
        Log.i("REGISTRATION", "Going to register pet with request: " + requestBody);

        if (activity != null) {
            String url = optUrl == null ? PETS_PATH + "/" + petId : optUrl;
            if (optUrl != null) resetOptUrlAndConnectionPermission();
            try {
                String response = new PutRequest().execute(new PostRequestParams(url, requestBody)).get();
                Log.i("VOLLEY", response);
                Toast.makeText(getContext(), "Информация о питомце успешно обновлена", Toast.LENGTH_LONG).show();
                NavController navController = Navigation.findNavController(root);
                navController.navigateUp();
            } catch (ExecutionException | InterruptedException | IllegalStateException error) {
                Log.e("VOLLEY", "Making put request (update pet): error - " + error.toString());
            }
        }
    }

    private void showPetInfo() {
        imagesBitmap.addAll(petInfo.getIcons());
        if (!imagesBitmap.isEmpty()) {
            petImages.setImageListener(new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_START);
                    imageView.setImageBitmap(imagesBitmap.get(position));
                }
            });
            petImages.setPageCount(imagesBitmap.size());
        }

        petName.setText(petInfo.getName());
        petAge.setText(petInfo.getAge());
        petBreed.setText(petInfo.getBreed());
        petComment.setText(petInfo.getComment());
        if (petInfo.getGender().equalsIgnoreCase("FEMALE")) {
            rgGender.check(R.id.radio_button_female_pet);
        } else {
            rgGender.check(R.id.radio_button_male_pet);
        }

        petType.setSelection(1);
        petPurpose.setSelection(2);
    }

    private PetInfo getPetsFromJSON(String jsonString) throws JSONException {
        return PetInfoUtils.getPetFromJSON(jsonString, 1, false);
    }

    public String getPetName() {
        return petName.getText().toString();
    }

    public String getPetAge() {
        return petAge.getText().toString();
    }

    public TextView getPetAgeObject() {
        return petAge;
    }

    public String getPetBreed() {
        return petBreed.getText().toString();
    }

    public String getPetComment() {
        return petComment.getText().toString();
    }

    public int getPetImagesCount() {
        return petImages.getPageCount();
    }

    public Button getUpdatePet() {
        return updatePet;
    }

    public Button getDeletePet() {
        return deletePet;
    }

    public PetInfo getPetInfo() {
        return petInfo;
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
