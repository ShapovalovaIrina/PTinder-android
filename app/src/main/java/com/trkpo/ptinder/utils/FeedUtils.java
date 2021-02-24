package com.trkpo.ptinder.utils;

import android.graphics.Bitmap;

import com.trkpo.ptinder.config.FeedTask;
import com.trkpo.ptinder.pojo.Feed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FeedUtils {
    public static Collection<Feed> getNewsFromJSON(String response) throws JSONException {
        List<Feed> collectedFeed = new ArrayList<>();
        JSONArray jArray = new JSONArray(response);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject topic = jArray.getJSONObject(i);
            String author = topic.getString("author");
            String imageUrl = topic.getString("content");
            String rScore = topic.getString("score");
            String title = topic.getString("title");
            Bitmap img = null;
            try {
                img = new FeedTask().execute(imageUrl).get();
            } catch (ExecutionException | InterruptedException | NullPointerException e) {
                e.printStackTrace();
            }
            collectedFeed.add(new Feed(author, rScore, title, img));
        }
        return collectedFeed;
    }
}
