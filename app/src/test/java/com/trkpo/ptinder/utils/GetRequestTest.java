package com.trkpo.ptinder.utils;

import android.os.Build;

import androidx.test.runner.AndroidJUnit4;

import com.trkpo.ptinder.utils.GetRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class GetRequestTest {
    private MockWebServer server = new MockWebServer();
    private String body = "Text response from server";

    @Before
    public void serverSetupUp() throws IOException {
        server.start(8080);
    }

    @After
    public void serverShutDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void correctGetRequestResponse() throws ExecutionException, InterruptedException {
        server.enqueue(new MockResponse().setBody(body));
        String url = server.url("/").toString();

        String response = new GetRequest().execute(url).get();
        assertEquals(body, response);
    }
}
