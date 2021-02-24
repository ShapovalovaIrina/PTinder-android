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
import com.trkpo.ptinder.utils.Connection;
import com.trkpo.ptinder.utils.NotificationUtils;

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
        if (!Connection.hasConnection(activity)) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(activity);

        String requestUrl = NOTIFICATIONS_PATH + "/" + googleId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("VOLLEY", "Get notifications: " + response);
                            notificationCardAdapter.setItems(NotificationUtils.getNotificationsFromJSON(response));
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
}
