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

import com.trkpo.ptinder.R;
import com.trkpo.ptinder.adapter.FeedCardAdapter;
import com.trkpo.ptinder.HTTP.Connection;
import com.trkpo.ptinder.utils.FeedUtils;
import com.trkpo.ptinder.HTTP.GetRequest;

import org.json.JSONException;

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

        setUpUrlForRequest();
    }

    public void loadFeeds(String... testUrl) {
        boolean connectionPermission = testUrl.length == 2 ? Boolean.valueOf(testUrl[1]) : true;
        if (!Connection.hasConnection(activity) || !connectionPermission) {
            Toast.makeText(activity, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        String url = testUrl.length == 0 ? NEWS_PATH : testUrl[0];
        try {
            String response = new GetRequest().execute(url).get();
            Log.d("VOLLEY", "Get news: " + response);
            feedCardAdapter.setItems(FeedUtils.getNewsFromJSON(response));
        } catch (JSONException | ExecutionException | InterruptedException error) {
            error.printStackTrace();
            Toast.makeText(activity, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public FeedCardAdapter getFeedCardAdapter() {
        return feedCardAdapter;
    }

    private void setUpUrlForRequest() {
        if (getArguments() != null) {
            String optUrl = (String) getArguments().getSerializable("optUrl");
            loadFeeds(optUrl);
        }
    }
}