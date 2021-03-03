package com.trkpo.ptinder.HTTP;

import android.os.AsyncTask;
import android.util.Log;

import com.trkpo.ptinder.HTTP.PostRequestParams;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostRequest extends AsyncTask<PostRequestParams, Void, String> {
    @Override
    protected String doInBackground(PostRequestParams... postRequestParams) {
        System.out.println("Post Request on url " + postRequestParams[0].getUrl());
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();


        RequestBody body = postRequestParams[0].getStringJsonBody() != null ?
                RequestBody.create(MediaType.parse("application/json"), postRequestParams[0].getStringJsonBody()) :
                RequestBody.create(MediaType.parse("application/json"), "");

        Request request = new Request.Builder()
                .url(postRequestParams[0].getUrl())
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                return Objects.requireNonNull(response.body()).string();
            } else {
                Log.d("Post request", "Have request answer with code " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
