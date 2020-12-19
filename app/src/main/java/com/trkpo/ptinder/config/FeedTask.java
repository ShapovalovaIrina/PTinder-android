package com.trkpo.ptinder.config;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FeedTask extends AsyncTask<String, Void, Bitmap> {

    private Exception exception;

    protected Bitmap doInBackground(String... urls) {
        try {
            Log.i("URL", "GOT url " + urls[0]);
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Bitmap feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}
