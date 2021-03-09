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

import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.NotificationCardAdapter;
import com.trkpo.ptinder.HTTP.Connection;
import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.utils.NotificationUtils;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.NOTIFICATIONS_PATH;

public class NotificationsFragment extends Fragment {
    private Activity activity;
    private View root;
    private RecyclerView notificationCardRecycleView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NotificationCardAdapter notificationCardAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notifications, container, false);
        activity = getActivity();
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpGoogleIdAndUrlForRequest();
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

        setUpGoogleIdAndUrlForRequest();
    }

    private void setUpGoogleIdAndUrlForRequest() {
        String googleId = (String) (getArguments() != null ? getArguments().getSerializable("googleId") : null);
        if (googleId != null) {
            String optUrl = (String) getArguments().getSerializable("optUrl");
            loadNotifications(googleId, optUrl);
        }
    }

    public void loadNotifications(String googleId, String ... optUrl) {
        boolean connectionPermission = optUrl.length == 2 ? Boolean.valueOf(optUrl[1]) : true;
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl.length == 0 ? NOTIFICATIONS_PATH + "/" + googleId : optUrl[0];
        try {
            String response = new GetRequest().execute(url).get();
            Log.d("VOLLEY", "Get notifications: " + response);
            notificationCardAdapter.setItems(NotificationUtils.getNotificationsFromJSON(response));
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
            Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public NotificationCardAdapter getNotificationCardAdapter() {
        return notificationCardAdapter;
    }

    public RecyclerView getNotificationCardRecycleView() {
        return notificationCardRecycleView;
    }
}
