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
import android.widget.ArrayAdapter;
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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.HTTP.Connection;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;

public class PetRegistrationFragment extends Fragment {
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

    private Button savePetBtn;
    private Button addPetTypeBtn;

    private CarouselView petImages;

    private List<Bitmap> imagesBitmap = new ArrayList<>();

    static final int GALLERY_REQUEST = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_pet_registration, container, false);
        activity = getActivity();
        petName = root.findViewById(R.id.pet_name);
        petAge = root.findViewById(R.id.pet_age);
        petBreed = root.findViewById(R.id.pet_breed);
        petComment = root.findViewById(R.id.comment);
        rgGender = root.findViewById(R.id.radio_group_gender_pet);
        petType = root.findViewById(R.id.type_spinner);
        petPurpose = root.findViewById(R.id.purpose_spinner);
        savePetBtn = root.findViewById(R.id.save_pet);
        addPetTypeBtn = root.findViewById(R.id.add_type_btn);

        initUserInfo();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setTypes(adapter);

        addPetTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimalTypeDialogFragment.showOpenDialog(activity, adapter);
            }
        });
        petType.setAdapter(adapter);

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
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(photoPickerIntent, "ChooseFile"), GALLERY_REQUEST);
            }
        });

        savePetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!Connection.hasConnection(activity)) {
                    Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject requestObject = PetInfoUtils.setPetToJSON(
                        petName.getText().toString(),
                        petAge.getText().toString(),
                        rgGender.getCheckedRadioButtonId() == 0 ? "FEMALE" : "MALE",
                        petType.getSelectedItem().toString(),
                        petBreed.getText().toString(),
                        petPurpose.getSelectedItem().toString(),
                        petComment.getText().toString(),
                        imagesBitmap,
                        googleId
                );
                final String requestBody = requestObject.toString();
                Log.i("REGISTRATION", "Going to register pet with request: " + requestBody);

                if (activity != null) {
                    RequestQueue queue = Volley.newRequestQueue(activity);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, PETS_PATH, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("VOLLEY", response);
                            NavController navController = Navigation.findNavController(v);
                            navController.navigateUp();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", error.toString());
                            NavController navController = Navigation.findNavController(v);
                            navController.navigateUp();
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                return null;
                            }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString = "";
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                            }
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };
                    queue.add(stringRequest);
                }
            }
        });

        return root;
    }

    private void setTypes(final ArrayAdapter<String> adapter) {
        if (!Connection.hasConnection(activity)) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        final RequestQueue queue = Volley.newRequestQueue(activity);
        final String url = PETS_PATH + "/types";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            adapter.addAll(getTypesFromJSON(response));
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
        petType.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == GALLERY_REQUEST) && (resultCode == RESULT_OK)) {
            if (data != null) {
                petImages.setImageListener(new ImageListener() {
                    @Override
                    public void setImageForPosition(int position, ImageView imageView) {
                        imageView.setScaleType(ImageView.ScaleType.FIT_START);
                        imageView.setImageBitmap(imagesBitmap.get(position));
                    }
                });
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

    private Collection<String> getTypesFromJSON(String response) throws JSONException {
        Collection<String> types = new ArrayList<>();
        JSONArray jArray = new JSONArray(response);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonObject = jArray.getJSONObject(i);
            String name = jsonObject.getString("type");
            types.add(name);
        }
        Log.i("TEST_INFO", "Got types in func " + types);

        return types;
    }

    private void initUserInfo() {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (signInAccount != null) {
            googleId = signInAccount.getId();
        }
    }
}
