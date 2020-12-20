package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.PetInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_pet_settings, container, false);
        activity = getActivity();

        petInfo = (PetInfo) getArguments().getSerializable("petInfo");
        petName = root.findViewById(R.id.pet_name);
        petAge = root.findViewById(R.id.pet_age);
        petBreed = root.findViewById(R.id.pet_breed);
        petComment = root.findViewById(R.id.comment);
        rgGender = root.findViewById(R.id.radio_group_gender_pet);
        petType = root.findViewById(R.id.type_spinner);
        petPurpose = root.findViewById(R.id.purpose_spinner);
        updatePet = root.findViewById(R.id.update_pet);
        deletePet = root.findViewById(R.id.delete_pet);
        petImages = root.findViewById(R.id.carousel_view);
        petImages.setPageCount(1);
        petImages.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                imageView.setImageResource(R.drawable.no_photo);
            }
        });

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

        updatePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject requestObject = new JSONObject();
                JSONObject jsonBodyWithPet = new JSONObject();
                try {
                    jsonBodyWithPet.put("name", petName.getText());
                    jsonBodyWithPet.put("age", Integer.parseInt(petAge.getText().toString()));
                    jsonBodyWithPet.put("gender", rgGender.getCheckedRadioButtonId() == 0 ? "FEMALE" : "MALE");
                    jsonBodyWithPet.put("type", (String) petType.getSelectedItem());
                    jsonBodyWithPet.put("breed", "" + petBreed.getText());
                    jsonBodyWithPet.put("purpose", translatePurpose(petPurpose.getSelectedItem().toString()));
                    jsonBodyWithPet.put("comment", petComment.getText());

                    JSONArray jsonWithPhotos = new JSONArray();
                    if (imagesBitmap != null) {
                        for (Bitmap image : imagesBitmap) {
                            byte[] imageBytes = getByteArrayFromBitmap(image);
                            String imageStr = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                            JSONObject jsonPhoto = new JSONObject();
                            jsonPhoto.put("photo", imageStr);
                            jsonWithPhotos.put(jsonPhoto);
                        }
                    }
                    requestObject.put("photos", jsonWithPhotos);
                    requestObject.put("type", (String) petType.getSelectedItem());
                    requestObject.put("googleId", "");
                    requestObject.put("pet", jsonBodyWithPet);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String requestBody = requestObject.toString();
                Log.i("REGISTRATION", "Going to register pet with request: " + requestBody);

                if (activity != null) {
                    RequestQueue queue = Volley.newRequestQueue(activity);

                    StringRequest stringRequest = new StringRequest(Request.Method.PUT, PETS_PATH + "/" + petInfo.getId(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("VOLLEY", response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", error.toString());
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

        deletePet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(activity);
                final String requestBody = "";
                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, PETS_PATH + "/" + petInfo.getId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("volley", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
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
                            responseString = String.valueOf(response.statusCode); // can get more details such as response.headers
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };
                queue.add(stringRequest);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.nav_settings);
            }
        });

        return root;
    }

    private String translatePurpose(String purpose) {
        if (purpose.equals("Прогулка")) {
            return "WALKING";
        }
        if (purpose.equals("Вязка")) {
            return "BREEDING";
        }
        if (purpose.equals("Переливание крови")) {
            return "DONORSHIP";
        }
        if (purpose.equals("Дружба")) {
            return "FRIENDSHIP";
        }
        if (purpose.equals("-")) {
            return "NOTHING";
        }
        return "";
    }

    private byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        return bos.toByteArray();
    }
}
