package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.PetCardAdapter;
import com.trkpo.ptinder.config.PhotoTask;
import com.trkpo.ptinder.utils.Connection;
import com.trkpo.ptinder.utils.PetInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.CONTACT_PATH;
import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static com.trkpo.ptinder.config.Constants.PETS_PATH;
import static com.trkpo.ptinder.config.Constants.SUBSCRIPTION_PATH;
import static com.trkpo.ptinder.config.Constants.USERS_PATH;

public class OtherUserProfileFragment extends Fragment {

    private Activity activity;
    private View root;

    private String currentUserGoogleId;

    private String userGoogleId;
    private ImageView userIcon;
    private ImageView userSubscribe;
    private ImageView requestUserContacts;
    private TextView username;
    private TextView location;
    private String userImageUrl;
    private TextView phone;
    private TextView email;
    private RelativeLayout phoneLayout;
    private RelativeLayout emailLayout;
    private RecyclerView petCardRecycleView;
    private PetCardAdapter petCardAdapter;

    private boolean isSubscr;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_other_user_profile, container, false);
        activity = getActivity();
        userIcon = root.findViewById(R.id.user_icon);
        username = root.findViewById(R.id.username);
        location = root.findViewById(R.id.location);
        phone = root.findViewById(R.id.user_phone);
        email = root.findViewById(R.id.user_email);
        phoneLayout = root.findViewById(R.id.other_user_profile_phone_layout);
        emailLayout = root.findViewById(R.id.other_user_profile_email_layout);
        userSubscribe = root.findViewById(R.id.user_subscribe);
        requestUserContacts = root.findViewById(R.id.request_for_user_contacts);

        currentUserGoogleId = GoogleSignIn.getLastSignedInAccount(activity).getId();

        initUserInfo();

        userSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSubscr) {
                    subscribeOnUser();
                } else {
                    if (activity != null) {
                        unsubscribeOnUser();
                    }
                }
            }
        });

        requestUserContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestUserContacts();
            }
        });

        initRecycleView();

        return root;
    }

    private void requestUserContacts() {
        if (!Connection.hasConnection(activity)) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = CONTACT_PATH + "/request/" + currentUserGoogleId + "/" + userGoogleId;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("VOLLEY", "Success response (request user info) from " + currentUserGoogleId + " to " + userGoogleId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "Not Success response (request user info): " + error.toString());
            }
        });
        queue.add(stringRequest);
    }

    private void subscribeOnUser() {
        if (!Connection.hasConnection(activity)) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("googleId", userGoogleId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = requestObject.toString();

        if (activity != null) {
            RequestQueue queue = Volley.newRequestQueue(activity);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, SUBSCRIPTION_PATH + "/" + currentUserGoogleId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("SUBSCRIPTION", "Successfully subscribed on user " + userGoogleId);
                    isSubscr = true;
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
            userSubscribe.setColorFilter(userIcon.getContext().getResources().getColor(R.color.colorIsSubscribed));
        }
        isSubscr = true;
    }

    private void unsubscribeOnUser() {
        if (!Connection.hasConnection(activity)) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = SUBSCRIPTION_PATH + "/" + currentUserGoogleId + "/" + userGoogleId;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isSubscr = false;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", "Not Success response (delete from favourite): " + error.toString());
            }
        });
        queue.add(stringRequest);
        isSubscr = false;
        userSubscribe.setColorFilter(userIcon.getContext().getResources().getColor(R.color.colorNotFavourite));
    }

    private void checkSubscription() {
        if (activity != null) {
            if (!Connection.hasConnection(activity)) {
                Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
                return;
            }
            RequestQueue queue = Volley.newRequestQueue(activity);
            String url = SUBSCRIPTION_PATH + "/check/" + currentUserGoogleId + "/" + userGoogleId;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            isSubscr = response.contains("true");
                            if (isSubscr) {
                                userSubscribe.setColorFilter(userIcon.getContext().getResources().getColor(R.color.colorIsSubscribed));
                            } else {
                                userSubscribe.setColorFilter(userIcon.getContext().getResources().getColor(R.color.colorNotFavourite));
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
        }
    }

    private void showInfo() {
        if (!Connection.hasConnection(activity)) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = USERS_PATH + "/" + userGoogleId;

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        try {
                            username.setText(jsonResponse.getString("firstName") + " " + jsonResponse.getString("lastName"));
                            location.setText(jsonResponse.getString("address"));
                            try {
                                userIcon.setImageBitmap(new PhotoTask().execute(jsonResponse.getString("photoUrl")).get());
                            } catch (ExecutionException | InterruptedException e) {
                                Log.e("BITMAP", "Got error during bitmap parsing" + e.toString());
                            }
                            boolean isContactInfoPublic = jsonResponse.getBoolean("contactInfoPublic");
                            if (isContactInfoPublic) {
                                email.setText(jsonResponse.getString("email"));
                                if (!jsonResponse.getString("number").equals("")) {
                                    phone.setText(jsonResponse.getString("number"));
                                } else {
                                    phone.setText("-");
                                }
                                emailLayout.setVisibility(View.VISIBLE);
                                phoneLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            Log.e("VOLLEY", "Making get request (get user by google id): json error - " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "Making get request (get user by google id): request error - " + error.toString());
            }
        });
        queue.add(stringRequest);
    }

    private void initUserInfo() {
        userGoogleId = getArguments().getString("googleId");
        checkSubscription();
        showInfo();
    }

    private void initRecycleView() {
        petCardRecycleView = root.findViewById(R.id.pet_cards_recycle_view);
        petCardRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        petCardAdapter = new PetCardAdapter();
        petCardRecycleView.setAdapter(petCardAdapter);

        loadFavouriteId();
    }

    private void loadFavouriteId() {
        if (!Connection.hasConnection(activity)) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = FAVOURITE_PATH + "/user/id/" + currentUserGoogleId;

        JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d("VOLLEY", "Making get request (load favourite pets id): response - " + response.toString());
                            List<Long> favouritePetsId = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                favouritePetsId.add(Long.valueOf(response.get(i).toString()));
                            }
                            loadPets(favouritePetsId);
                        } catch (JSONException e) {
                            Log.e("VOLLEY", "Making get request (load favourite pets id): json error - " + e.toString());
                            Toast.makeText(activity, "JSON exception: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "Making get request (load favourite pets id): request error - " + error.toString());
                Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private void loadPets(final List<Long> favouritePetsId) {
        if (!Connection.hasConnection(activity)) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = PETS_PATH + "/owner/" + userGoogleId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("VOLLEY", "Making get request (load pets): response - " + response.toString());
                            petCardAdapter.setItems(PetInfoUtils.getPetsFromJSON(response, favouritePetsId, currentUserGoogleId, 4));
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
    }
}
