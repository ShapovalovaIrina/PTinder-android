package com.trkpo.ptinder.ui;

import android.app.Activity;
import android.graphics.Bitmap;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.FeedCardAdapter;
import com.trkpo.ptinder.config.FeedTask;
import com.trkpo.ptinder.pojo.Feed;
import com.trkpo.ptinder.utils.Connection;
import com.trkpo.ptinder.utils.FeedUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.NEWS_PATH;

public class FeedFragment extends Fragment {

    private Activity activity;
    private View root;

    private RecyclerView feedCardRecycleView;
    private FeedCardAdapter feedCardAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_feed, container, false);
        activity = getActivity();

        initRecycleView();
        return root;
    }

    private void initRecycleView() {
        feedCardRecycleView = root.findViewById(R.id.feed_cards_recycle_view);
        feedCardRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        feedCardAdapter = new FeedCardAdapter();
        feedCardRecycleView.setAdapter(feedCardAdapter);

        loadFeeds();
    }

    private void loadFeeds() {
        if (!Connection.hasConnection(activity)) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(activity);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, NEWS_PATH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            feedCardAdapter.setItems(FeedUtils.getNewsFromJSON(response));
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