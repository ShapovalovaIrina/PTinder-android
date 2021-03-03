package com.trkpo.ptinder.HTTP;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeleteRequest extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        System.out.println("Delete Request on url " + urls[0]);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(urls[0])
                .delete()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
