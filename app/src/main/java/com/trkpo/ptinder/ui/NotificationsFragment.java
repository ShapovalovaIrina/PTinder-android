package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.NotificationCardAdapter;
import com.trkpo.ptinder.pojo.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.trkpo.ptinder.config.Constants.NOTIFICATIONS_PATH;

public class NotificationsFragment extends Fragment {
    private Activity activity;
    private View root;
    private String googleId;
    private RecyclerView notificationCardRecycleView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NotificationCardAdapter notificationCardAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notifications, container, false);
        activity = getActivity();
        googleId = (String) getArguments().getSerializable("googleId");
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNotifications();
                Toast.makeText(getContext(), "Refresh", Toast.LENGTH_SHORT).show();
            }
        });
        initRecycleView();
        return root;
    }

    private void initRecycleView() {
        notificationCardRecycleView = root.findViewById(R.id.notification_cards_recycle_view);
        notificationCardRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        notificationCardAdapter = new NotificationCardAdapter();
        notificationCardRecycleView.setAdapter(notificationCardAdapter);

        loadNotifications();
    }

    private void loadNotifications() {
        RequestQueue queue = Volley.newRequestQueue(activity);

        String requestUrl = NOTIFICATIONS_PATH + "/" + googleId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("VOLLEY", "Get notifications: " + response);
                            notificationCardAdapter.setItems(getNotificationsFromJSON(response));
                        } catch (JSONException e) {
                            Log.e("VOLLEY", "Making get request (load notifications): json error - " + e.toString());
                            Toast.makeText(activity, "JSON exception: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", "Making get request (load notifications): request error - " + error.toString());
                Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private Collection<Notification> getNotificationsFromJSON(String response) throws JSONException {
        List<Notification> notifications = new ArrayList<>();
        JSONArray jArray = new JSONArray(response);

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonNotification = jArray.getJSONObject(i);
            String title = jsonNotification.getString("type");
            String id = jsonNotification.getString("id");
            String text = jsonNotification.getString("text");
            boolean isRead = jsonNotification.getBoolean("read");
            String addresseeGoogleId = jsonNotification.getJSONObject("addressee").getString("googleId");
            String addresseeFromGoogleId = jsonNotification.getString("addresseeFromId");
            notifications.add(new Notification(id, title, text, isRead, addresseeGoogleId, addresseeFromGoogleId));
        }
        return notifications;
    }
}
