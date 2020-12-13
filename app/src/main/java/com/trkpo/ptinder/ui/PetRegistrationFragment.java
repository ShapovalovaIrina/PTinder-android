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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.trkpo.ptinder.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import static com.trkpo.ptinder.config.Constants.PETS_PATH;

public class PetRegistrationFragment extends Fragment {
    private Activity activity;
    private View root;
    private TextView petName;
    private TextView petAge;
    private Spinner petGender;
    private Spinner petType;
    private String googleId;

    private Button savePetBtn;

    private ImageView petImage;

    private Bitmap imageBitmap;

    static final int GALLERY_REQUEST = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_pet_registration, container, false);
        activity = getActivity();
        petName = root.findViewById(R.id.pet_name);
        petAge = root.findViewById(R.id.pet_age);
        petGender = root.findViewById(R.id.gender_spinner);
        petType = root.findViewById(R.id.type_spinner);
        savePetBtn = root.findViewById(R.id.save_pet);
        petImage = root.findViewById(R.id.pet_icon);
        initUserInfo();

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
                    jsonBodyWithPet.put("gender", translateGender((String) petGender.getSelectedItem()));
                    jsonBodyWithPet.put("type", translateType((String) petType.getSelectedItem()));
                    if (imageBitmap != null) {
                        byte[] imageBytes = getByteArrayFromBitmap(imageBitmap);
                        String imageStr = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        jsonBodyWithPet.put("photo", imageStr);
                    }
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

    private String translateType(String type) {
        if (type.equals("Кот")) {
            return "CAT";
        }
        if (type.equals("Собака")) {
            return "DOG";
        }
        if (type.equals("Рыбка")) {
            return "FISH";
        }
        if (type.equals("Хомяк")) {
            return "HAMSTER";
        }
        if (type.equals("Мышь")) {
            return "MOUSE";
        }
        if (type.equals("Крыса")) {
            return "RAT";
        }
        if (type.equals("Змея")) {
            return "SNAKE";
        }
        if (type.equals("Попугай")) {
            return "PARROT";
        }
        return "";
    }

    private String translateGender(String gender) {
        return gender.equals("Мужской") ? "MALE" : "FEMALE";
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
