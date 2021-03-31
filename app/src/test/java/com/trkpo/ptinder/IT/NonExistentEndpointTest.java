package com.trkpo.ptinder.IT;

import android.os.Build;

import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.HTTP.GetRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

import static com.trkpo.ptinder.config.Constants.SERVER_PATH;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class NonExistentEndpointTest {

    @Test
    public void testRequestToNonExistentEndpoint() {
        try {
            String response = new GetRequest().execute(SERVER_PATH + "/empty").get();
            JSONObject JSONResponse = new JSONObject(response);
            assertEquals(404, JSONResponse.getInt("status"));
            assertEquals("Not Found", JSONResponse.getString("error"));
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
}
