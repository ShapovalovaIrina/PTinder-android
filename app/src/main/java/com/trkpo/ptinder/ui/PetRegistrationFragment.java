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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.Gender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.trkpo.ptinder.config.Constants.PETS_PATH;

public class PetRegistrationFragment extends Fragment {
    private Activity activity;
    private View root;
    private TextView petName;
    private TextView petAge;
    private TextView petBreed;
    private TextView petComment;
    private Gender petGender;
    private Spinner petType;
    private Spinner petPurpose;
    private String googleId;
    private RadioGroup rgGender;

    private Button savePetBtn;
    private Button addPetTypeBtn;

    private ImageView petImage;

    private Bitmap imageBitmap;

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
        petImage = root.findViewById(R.id.pet_icon);

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radio_button_female_pet:
                        petGender = Gender.FEMALE;
                        break;
                    case R.id.radio_button_male_pet:
                        petGender = Gender.MALE;
                        break;
                    default:
                        break;
                }
            }
        });
        initUserInfo();

        final RequestQueue queue = Volley.newRequestQueue(activity);
        final String url = PETS_PATH + "/types";
        final List<String> types = new ArrayList<>();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

        addPetTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimalTypeDialogFragment.showOpenDialog(activity, adapter);
            }
        });
        petType.setAdapter(adapter);


        petImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);

//                Uri selectedImage = imageReturnedIntent.getData();
//                try {
//                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                petImage.setImageBitmap(imageBitmap);
            }

        });

        savePetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject requestObject = new JSONObject();
                JSONObject jsonBodyWithPet = new JSONObject();
                try {
                    jsonBodyWithPet.put("name", petName.getText());
                    jsonBodyWithPet.put("age", petAge.getText());
                    jsonBodyWithPet.put("gender", petGender);
                    jsonBodyWithPet.put("type", (String) petType.getSelectedItem());
                    jsonBodyWithPet.put("breed",  "" + petBreed.getText());
                    jsonBodyWithPet.put("purpose", translatePurpose(petPurpose.getSelectedItem().toString()));
                    jsonBodyWithPet.put("comment", petComment.getText());
                    if (imageBitmap != null) {
                        byte[] imageBytes = getByteArrayFromBitmap(imageBitmap);
                        String imageStr = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        jsonBodyWithPet.put("photo", imageStr);
                    }
                    requestObject.put("type", (String) petType.getSelectedItem());
                    requestObject.put("googleId", googleId);
                    requestObject.put("pet", jsonBodyWithPet);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String requestBody = requestObject.toString();
                Log.i("REGISTRATION", "Going to register pet with request: " + requestBody);

                if (activity != null) {
                    RequestQueue queue = Volley.newRequestQueue(activity);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, PETS_PATH, new Response.Listener<String>() {
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
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.nav_user_profile);
            }
        });

        return root;
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

    private byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos);
        return bos.toByteArray();
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
        return "";
    }


}
